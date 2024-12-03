package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.ApiClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    @Value("${service.config.clients.monitoring.basePath}")
    String eaiBasePath;

    private final ApiClient eaiApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
    }
}
