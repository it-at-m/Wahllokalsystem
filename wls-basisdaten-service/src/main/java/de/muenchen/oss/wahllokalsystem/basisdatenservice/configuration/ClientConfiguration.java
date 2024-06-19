package de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.ApiClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.api.WahlvorschlagControllerApi;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.WlsResponseErrorHandler;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {

    @Value("${app.clients.eai.basePath}")
    String eaiBasePath;

    @Bean
    public WlsResponseErrorHandler wlsResponseErrorHandler(final ObjectMapper objectMapper) {
        return new WlsResponseErrorHandler(objectMapper);
    }

    @Bean
    public WahlvorschlagControllerApi wahlvorschlagControllerApi(final ApiClient apiClient) {
        return new WahlvorschlagControllerApi(apiClient);
    }

    @Bean
    public ApiClient eaiApiClient(final RestTemplate webClient) {
        val eaiApiClient = new ApiClient(webClient);
        eaiApiClient.setBasePath(eaiBasePath);
        return eaiApiClient;
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

            if (!(authentication.getCredentials() instanceof AbstractOAuth2Token)) {
                return execution.execute(request, body);
            }

            val token = (AbstractOAuth2Token) authentication.getCredentials();
            request.getHeaders().setBearerAuth(token.getTokenValue());
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
