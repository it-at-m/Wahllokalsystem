package de.muenchen.oss.wahllokalsystem.wls.common.monitoring.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@AutoConfiguration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableAspectJAutoProxy
public class WlsLoggingAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(ObjectMapper.class)
  public RequestMdcLoggingAspect requestMdcLoggingAspect(ObjectMapper objectMapper) {
    return new RequestMdcLoggingAspect(objectMapper);
  }
}
