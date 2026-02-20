package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.WlsResponseErrorHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.security.OAuth2TokenInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {

  @Bean
  public RestTemplate restTemplate(
      final RestTemplateBuilder builder,
      final WlsResponseErrorHandler wlsResponseErrorHandler,
      final OAuth2TokenInterceptor oAuth2TokenInterceptor,
      final ObjectMapper objectMapper) {

    return builder
        .additionalMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
        .errorHandler(wlsResponseErrorHandler)
        .additionalInterceptors(oAuth2TokenInterceptor)
        .build();
  }

  @Bean
  public WlsResponseErrorHandler wlsResponseErrorHandler(final ObjectMapper objectMapper) {
    return new WlsResponseErrorHandler(objectMapper);
  }
}
