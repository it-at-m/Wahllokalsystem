package de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.ApiClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.api.WahlvorschlagControllerApi;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {

    @Value("${app.clients.eai.basePath}")
    String eaiBasePath;

    @Bean
    public WahlvorschlagControllerApi wahlvorschlagControllerApi(final ApiClient apiClient) {
        return new WahlvorschlagControllerApi(apiClient);
    }

    @Bean
    public ApiClient eaiApiClient(final WebClient webClient) {
        val eaiApiClient = new ApiClient(webClient);
        eaiApiClient.setBasePath(eaiBasePath);
        return eaiApiClient;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(eaiBasePath)
                .filter(new ServletBearerExchangeFilterFunction())
                .build();
    }
}
