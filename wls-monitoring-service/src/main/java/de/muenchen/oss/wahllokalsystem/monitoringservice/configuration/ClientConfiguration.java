package de.muenchen.oss.wahllokalsystem.monitoringservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.WlsResponseErrorHandler;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {

    @Bean
    public WlsResponseErrorHandler wlsResponseErrorHandler(final ObjectMapper objectMapper) {
        return new WlsResponseErrorHandler(objectMapper);
    }

    @Bean
    public RestTemplate restTemplate(final WlsResponseErrorHandler wlsResponseErrorHandler) {
        val restTemplate = new RestTemplate();

        restTemplate.setErrorHandler(wlsResponseErrorHandler);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return execution.execute(request, body);
            }

            if (!(authentication.getCredentials() instanceof AbstractOAuth2Token token)) {
                return execution.execute(request, body);
            }

            request.getHeaders().setBearerAuth(token.getTokenValue());
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
