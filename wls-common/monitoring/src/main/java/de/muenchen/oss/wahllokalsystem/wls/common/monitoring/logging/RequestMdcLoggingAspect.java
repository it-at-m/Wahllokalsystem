package de.muenchen.oss.wahllokalsystem.wls.common.monitoring.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Aspect
public class RequestMdcLoggingAspect {

  // Thread-sicherer Cache für die Parameternamen der Controller-Methoden (für die Performance)
  private static final ConcurrentHashMap<Method, String[]> parameterNameCache =
      new ConcurrentHashMap<>();

  private final ObjectMapper objectMapper;

  @Pointcut(
      "@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
  public void controllerMethods() {}

  @Around("controllerMethods()")
  public Object logRequestArgumentsToMdc(ProceedingJoinPoint joinPoint) throws Throwable {
    val signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    val className = signature.getDeclaringType().getSimpleName();
    val methodName = signature.getName();

    if (log.isDebugEnabled()) {
      log.debug("Eingehender Request in Controller: {} -> {}()", className, methodName);
    }

    Map<String, String> backupMdcContext = MDC.getCopyOfContextMap();
    val parameterValues = joinPoint.getArgs();

    val parameterNames =
        parameterNameCache.computeIfAbsent(method, this::resolveParameterNamesViaSpring);

    try {
      if (log.isTraceEnabled()) {
        fillMdcWithValues(parameterNames, parameterValues);
        log.trace("MDC erfolgreich mit Request-Payloads befüllt für: {}()", methodName);
      }

      return joinPoint.proceed();

    } catch (Throwable ex) {
      fillMdcWithValues(parameterNames, parameterValues);
      log.error(
          "Fehler bei der Verarbeitung im Controller {} -> {}(). Ursache: {}",
          className,
          methodName,
          ex.getMessage(),
          ex);
      throw ex;
    } finally {
      if (backupMdcContext == null) {
        MDC.clear();
      } else {
        MDC.setContextMap(backupMdcContext);
      }
    }
  }

  /**
   * NUTZT SPRING-LOGIK STATT JAVA-REFLECTION: Ermittelt die echten Namen der Parameter anhand der
   * Spring-Annotationen (@RequestParam, @RequestBody, @PathVariable). Falls keine Annotation
   * vorhanden ist und der Compiler Namen gelöscht hat, fällt es sicher auf arg0, arg1 zurück.
   */
  private String[] resolveParameterNamesViaSpring(Method method) {
    int paramCount = method.getParameterCount();
    String[] names = new String[paramCount];

    for (int i = 0; i < paramCount; i++) {
      MethodParameter methodParam = new SynthesizingMethodParameter(method, i);

      if (methodParam.hasParameterAnnotation(RequestParam.class)) {
        RequestParam ann = methodParam.getParameterAnnotation(RequestParam.class);
        if (ann != null && !ann.value().isEmpty()) {
          names[i] = ann.value();
          continue;
        }
        if (ann != null && !ann.name().isEmpty()) {
          names[i] = ann.name();
          continue;
        }
      }

      if (methodParam.hasParameterAnnotation(PathVariable.class)) {
        PathVariable ann = methodParam.getParameterAnnotation(PathVariable.class);
        if (ann != null && !ann.value().isEmpty()) {
          names[i] = ann.value();
          continue;
        }
        if (ann != null && !ann.name().isEmpty()) {
          names[i] = ann.name();
          continue;
        }
      }

      if (methodParam.hasParameterAnnotation(RequestBody.class)) {
        names[i] = "body";
        continue;
      }

      // Fallback, wenn keine Spring-Annotation greift: Nutze den MethodParameter-Namen
      methodParam.initParameterNameDiscovery(null);
      String discoveredName = methodParam.getParameterName();
      names[i] = (discoveredName != null) ? discoveredName : "arg" + i;
    }
    return names;
  }

  private void fillMdcWithValues(String[] parameterNames, Object[] parameterValues) {
    if (parameterNames == null || parameterValues == null) return;
    int limit = Math.min(parameterNames.length, parameterValues.length);
    for (int i = 0; i < limit; i++) {
      try {
        String paramName = parameterNames[i];
        Object paramValue = parameterValues[i];
        if (paramValue != null && !isTechnicalClass(paramValue.getClass())) {
          String mdcKey = "req.arg." + paramName;
          String serializedValue = serializeValue(paramValue);
          MDC.put(mdcKey, serializedValue);
        }
      } catch (Throwable t) {
        // Fehler beim MDC-Befüllen darf nicht den Requestfluss stören oder abbrechen
        log.warn(
            "MDC-Befüllung für Parameter an Index {} fehlgeschlagen. Ursache: {}",
            i,
            t.getMessage());
      }
    }
  }

  private boolean isTechnicalClass(Class<?> clazz) {
    if (clazz == null) {
      return true;
    }

    if (java.security.Principal.class.isAssignableFrom(clazz)
        || jakarta.servlet.ServletRequest.class.isAssignableFrom(clazz)
        || jakarta.servlet.ServletResponse.class.isAssignableFrom(clazz)
        || org.springframework.http.HttpEntity.class.isAssignableFrom(clazz)
        || org.springframework.validation.BindingResult.class.isAssignableFrom(clazz)
        || org.springframework.ui.Model.class.isAssignableFrom(clazz)
        || org.springframework.web.bind.support.SessionStatus.class.isAssignableFrom(clazz)) {
      return true;
    }

    try {
      if (org.springframework.security.core.Authentication.class.isAssignableFrom(clazz)) {
        return true;
      }
    } catch (NoClassDefFoundError ignored) {
      // Spring Security ist in diesem Laufzeit-Modul nicht geladen
    }

    String className = clazz.getName();
    if (className.startsWith("jakarta.servlet.")
        || className.startsWith("javax.servlet.")
        || className.startsWith("org.springframework.ui.")
        || className.startsWith("org.springframework.validation.")
        || className.startsWith("org.springframework.web.bind.support.")
        || className.startsWith("org.springframework.web.util.")) {
      return true;
    }
    return className.contains("$$EnhancerBySpringCGLIB")
        || className.contains("$MockitoMock")
        || className.contains("Proxy");
  }

  private String serializeValue(Object paramValue) {
    try {
      if (paramValue == null) {
        return "null";
      }
      if (paramValue instanceof String
          || paramValue instanceof Number
          || paramValue instanceof Boolean) {
        return paramValue.toString();
      }
      return objectMapper.writeValueAsString(paramValue);
    } catch (Throwable t) {
      return "[Serialization Failed: "
          + t.getClass().getSimpleName()
          + " - "
          + t.getMessage()
          + "]";
    }
  }
}
