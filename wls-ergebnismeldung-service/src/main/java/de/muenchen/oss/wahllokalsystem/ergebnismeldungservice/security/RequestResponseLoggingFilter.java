/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.security;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.logging.PerformanceLogging;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

/** This filter logs the username for requests. */
@Component
@Order(1)
@Slf4j
public class RequestResponseLoggingFilter implements Filter {

  private static final String REQUEST_LOGGING_MODE_ALL = "all";

  private static final String REQUEST_LOGGING_MODE_CHANGING = "changing";

  private static final List<String> CHANGING_METHODS =
      Arrays.asList("POST", "PUT", "PATCH", "DELETE");

  private static final Logger performanceLogger = PerformanceLogging.createPerformanceLogger(RequestResponseLoggingFilter.class.getName());

  /** The property or a zero length string if no property is available. */
  @Value("${security.logging.requests:}")
  private String requestLoggingMode;

  /** {@inheritDoc} */
  @Override
  public void init(final FilterConfig filterConfig) {
    log.debug("Initializing filter: {}", this);
  }

  /**
   * The method logs the username extracted out of the {@link SecurityContext}, the kind of
   * HTTP-Request, the targeted URI and the response http status code.
   *
   * <p>{@inheritDoc}
   */
  @Override
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    val stopWatch = StopWatch.createStarted();
    chain.doFilter(request, response);
    stopWatch.stop();

    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    performanceLogger.atInfo().log("Request on {} {} took {} ms", httpRequest.getMethod(), httpRequest.getRequestURI(), stopWatch.getDuration().toMillis());
    if (checkForLogging(httpRequest)) {
      log.info(
          "User {} executed {} on URI {} with http status {}",
          AuthUtils.getUsername(),
          httpRequest.getMethod(),
          httpRequest.getRequestURI(),
          httpResponse.getStatus());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void destroy() {
    log.debug("Destructing filter: {}", this);
  }

  /**
   * The method checks if logging the username should be done.
   *
   * @param httpServletRequest The request to check for logging.
   * @return True if logging should be done otherwise false.
   */
  private boolean checkForLogging(HttpServletRequest httpServletRequest) {
    return requestLoggingMode.equals(REQUEST_LOGGING_MODE_ALL)
        || (requestLoggingMode.equals(REQUEST_LOGGING_MODE_CHANGING)
            && CHANGING_METHODS.contains(httpServletRequest.getMethod()));
  }
}
