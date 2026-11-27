/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2026
 */
package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.ForwardedHeaderFilter;

class ForwardedHeaderConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(ForwardedHeaderConfiguration.class);

  @Nested
  class ForwardedHeaderFilterRegistration {

    @Test
    void should_useXForwardedHeadersAndPrefix_when_noFormatIsConfigured() {
      contextRunner.run(
          context -> {
            FilterRegistrationBean<?> registration = context.getBean(FilterRegistrationBean.class);
            Assertions.assertThat(registration.getUrlPatterns()).containsExactly("/*");
            var filter = (ForwardedHeaderFilter) registration.getFilter();
            var request = request();
            request.addHeader("X-Forwarded-Proto", "https");
            request.addHeader("X-Forwarded-Host", "external.example");
            request.addHeader("X-Forwarded-Prefix", "/proxy");
            request.addHeader("Forwarded", "proto=http;host=ignored.example");
            var chain = new MockFilterChain();

            filter.doFilter(request, new MockHttpServletResponse(), chain);

            var forwardedRequest = (HttpServletRequest) chain.getRequest();
            Assertions.assertThat(forwardedRequest.getScheme()).isEqualTo("https");
            Assertions.assertThat(forwardedRequest.getServerName()).isEqualTo("external.example");
            Assertions.assertThat(forwardedRequest.getContextPath()).isEqualTo("/proxy");
          });
    }

    @Test
    void should_useStandardForwardedHeaderAndPrefix_when_configured() {
      contextRunner
          .withPropertyValues("service.config.forwarded.use-standard-header=true")
          .run(
              context -> {
                FilterRegistrationBean<?> registration =
                    context.getBean(FilterRegistrationBean.class);
                var filter = (ForwardedHeaderFilter) registration.getFilter();
                var request = request();
                request.addHeader("Forwarded", "proto=https;host=standard.example");
                request.addHeader("X-Forwarded-Host", "ignored.example");
                request.addHeader("X-Forwarded-Prefix", "/proxy");
                var chain = new MockFilterChain();

                filter.doFilter(request, new MockHttpServletResponse(), chain);

                var forwardedRequest = (HttpServletRequest) chain.getRequest();
                Assertions.assertThat(forwardedRequest.getScheme()).isEqualTo("https");
                Assertions.assertThat(forwardedRequest.getServerName())
                    .isEqualTo("standard.example");
                Assertions.assertThat(forwardedRequest.getContextPath()).isEqualTo("/proxy");
              });
    }
  }

  private MockHttpServletRequest request() {
    var request = new MockHttpServletRequest("GET", "/resource");
    request.setServerName("internal.example");
    return request;
  }
}
