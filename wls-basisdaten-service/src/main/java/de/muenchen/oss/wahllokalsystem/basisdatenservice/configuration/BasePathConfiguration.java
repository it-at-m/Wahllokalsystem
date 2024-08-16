package de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    @Value("${app.clients.eai.basePath}")
    String eaiBasePath;

    @Value("${app.clients.infomanagement.basePath}")
    String infomanagementBasePath;

    private final de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.ApiClient eaiApiClient;
    private final de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.ApiClient infomanagementApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
        infomanagementApiClient.setBasePath(infomanagementBasePath);
    }
}
