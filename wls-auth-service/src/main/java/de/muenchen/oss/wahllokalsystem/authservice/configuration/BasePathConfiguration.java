package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.ApiClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    @Value("${serviceauth.clients.infomanagement.basepath}")
    String eaiBasePath;

    private final ApiClient eaiApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
    }
}
