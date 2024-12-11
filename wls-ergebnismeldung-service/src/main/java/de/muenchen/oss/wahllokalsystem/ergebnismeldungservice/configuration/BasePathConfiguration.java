package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    @Value("${service.config.clients.eai.basePath}")
    String eaiBasePath;

    @Value("${service.config.clients.monitoring.basePath}")
    String monitoringBasePath;

    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.ApiClient eaiApiClient;
    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.ApiClient monitoringApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
        monitoringApiClient.setBasePath(monitoringBasePath);
    }
}
