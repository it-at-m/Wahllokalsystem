package de.muenchen.oss.wahllokalsystem.adminservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    /* Umgebungsvariable welche die Ziel-URL enthält, z.b. http//localhost:39146 */
    @Value("${service.config.clients.auth.basePath}")
    String authBasePath;

    @Value("${service.config.clients.basisdaten.basePath}")
    String basisdatenBasePath;

    @Value("${service.config.clients.infomanagement.basePath}")
    String infomanagementBasePath;

    @Value("${service.config.clients.ergebnismeldung.basePath}")
    String ergebnismeldungBasePath;

    private final de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.ApiClient authApiClient;
    private final de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.ApiClient basisdatenApiClient;
    private final de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.ApiClient infomanagementApiClient;
    private final de.muenchen.oss.wahllokalsystem.adminservice.eai.ergebnismeldung.ApiClient ergebnismeldungApiClient;

    @PostConstruct
    public void updateBasePaths() {
        basisdatenApiClient.setBasePath(basisdatenBasePath);
        authApiClient.setBasePath(authBasePath);
        infomanagementApiClient.setBasePath(infomanagementBasePath);
        ergebnismeldungApiClient.setBasePath(ergebnismeldungBasePath);
    }
}
