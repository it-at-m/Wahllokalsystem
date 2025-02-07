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

    @Value("${service.config.clients.infomanagement.basePath}")
    String infomanagementBasePath;

    @Value("${service.config.clients.basisdaten.basePath}")
    String basisdatenBasePath;

    @Value("${service.config.clients.briefwahl.basePath}")
    String briefwahlBasePath;

    @Value("${service.config.clients.wahlvorbereitung.basePath}")
    String wahlvorbereitungBasePath;

    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.ApiClient eaiApiClient;
    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.ApiClient monitoringApiClient;
    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.ApiClient infomanagementApiClient;
    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.ApiClient basisdatentApiClient;
    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.ApiClient briefwahlApiClient;
    private final de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.wahlvorbereitung.ApiClient wahlvorbereitungApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
        monitoringApiClient.setBasePath(monitoringBasePath);
        infomanagementApiClient.setBasePath(infomanagementBasePath);
        basisdatentApiClient.setBasePath(basisdatenBasePath);
        briefwahlApiClient.setBasePath(briefwahlBasePath);
        wahlvorbereitungApiClient.setBasePath(wahlvorbereitungBasePath);
    }
}
