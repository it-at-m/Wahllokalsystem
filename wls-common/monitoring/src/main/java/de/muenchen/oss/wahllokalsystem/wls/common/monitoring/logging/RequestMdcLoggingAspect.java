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
    String methodName = signature.getName();

    if (log.isDebugEnabled()) {
      log.debug("Eingehender Request in Controller: {} -> {}()", className, methodName);
    }

    Map<String, String> backupMdcContext = MDC.getCopyOfContextMap();
    Object[] args = joinPoint.getArgs();

    String[] parameterNames =
        parameterNameCache.computeIfAbsent(method, this::resolveParameterNamesViaSpring);

    try {
      if (log.isTraceEnabled()) {
        fillMdcWithArguments(parameterNames, args);
        log.trace("MDC erfolgreich mit Request-Payloads befüllt für: {}()", methodName);
      }

      return joinPoint.proceed();

    } catch (Throwable ex) {
      fillMdcWithArguments(parameterNames, args);
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

  private void fillMdcWithArguments(String[] parameterNames, Object[] args) {
    if (parameterNames != null && args != null) {
      for (int i = 0; i < args.length; i++) {
        String paramName = parameterNames[i];
        Object argValue = args[i];

        if (argValue != null && !isTechnicalClass(argValue.getClass())) {
          String mdcKey = "req.arg." + paramName;
          String serializedValue = serializeArgument(argValue);
          MDC.put(mdcKey, serializedValue);
        }
      }
    }
  }

  private boolean isTechnicalClass(Class<?> clazz) {
    String className = clazz.getName();
    return className.startsWith("jakarta.servlet.")
        || className.startsWith("javax.servlet.")
        || className.startsWith("org.springframework.ui.")
        || className.startsWith("org.springframework.validation.")
        || className.startsWith("org.springframework.web.bind.support.")
        || className.startsWith("org.springframework.web.util.")
        || className.startsWith("org.springframework.http.HttpEntity")
        || className.startsWith("java.security.Principal")
        || className.startsWith("org.springframework.security.core.");
  }

  private String serializeArgument(Object arg) {
    try {
      if (arg instanceof String || arg instanceof Number || arg instanceof Boolean) {
        return arg.toString();
      }
      return objectMapper.writeValueAsString(arg);
    } catch (Exception e) {
      return "[Serialization Failed: " + e.getMessage() + "]";
    }
  }
}
