package de.muenchen.oss.wahllokalsystem.adminservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.WlsResponseErrorHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.security.OAuth2TokenInterceptor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {

    @Bean
    public RestTemplate restTemplate(final WlsResponseErrorHandler wlsResponseErrorHandler, final OAuth2TokenInterceptor oAuth2TokenInterceptor) {
        val restTemplate = new RestTemplate();

        /* definieren des Errorhandlers für Antworten vom externen Service */
        restTemplate.setErrorHandler(wlsResponseErrorHandler);
        /* Ergänzen eines Interceptors um den Bearer-Token an den nächsten Service weiter zu geben */
        restTemplate.getInterceptors().add(oAuth2TokenInterceptor);

        return restTemplate;
    }

    @Bean
    public WlsResponseErrorHandler wlsResponseErrorHandler(final ObjectMapper objectMapper) {
        return new WlsResponseErrorHandler(objectMapper);
    }

}