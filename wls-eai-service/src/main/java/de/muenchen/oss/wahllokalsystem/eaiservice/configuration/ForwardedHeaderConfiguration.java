/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

/** Configures the {@link ForwardedHeaderFilter} for the header format used by the trusted proxy. */
@Configuration
public class ForwardedHeaderConfiguration {

  @Bean
  public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter(
      @Value("${service.config.forwarded.use-standard-header:false}") boolean useStandardHeader) {
    final FilterRegistrationBean<ForwardedHeaderFilter> registration =
        new FilterRegistrationBean<>();
    final ForwardedHeaderFilter filter = new ForwardedHeaderFilter(useStandardHeader);
    filter.setUseForwardedPrefix(true);
    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    return registration;
  }
}
