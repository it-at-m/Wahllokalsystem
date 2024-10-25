package de.muenchen.oss.wahllokalsystem.monitoringservice.configuration;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.ApiClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    @Value("${app.clients.eai.basePath}")
    String eaiBasePath;

    private final ApiClient eaiApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
    }
}
