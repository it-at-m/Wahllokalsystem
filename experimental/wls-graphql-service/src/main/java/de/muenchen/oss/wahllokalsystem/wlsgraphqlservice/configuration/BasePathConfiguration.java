package de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.configuration;

import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.briefwahl.ApiClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    @Value("${service.config.clients.basisdaten.basePath}")
    String basisdatenBasePath;
    @Value("${service.config.clients.briefwahl.basePath}")
    String briefwahlBasePath;
    @Value("${service.config.clients.broadcast.basePath}")
    String broadcastBasePath;
    @Value("${service.config.clients.infomanagement.basePath}")
    String infomanagementBasePath;
    @Value("${service.config.clients.monitoring.basePath}")
    String monitoringBasePath;
    @Value("${service.config.clients.vorfaelle.basePath}")
    String vorfaelleBasePath;
    @Value("${service.config.clients.wahlvorbereitung.basePath}")
    String wahlvorbereitungBasePath;

    private final de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.basisdaten.ApiClient basisdatenApiClient;
    private final ApiClient briefwahlApiClient;
    private final de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.broadcast.ApiClient broadcastApiClient;
    private final de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.infomanagement.ApiClient infomanagementApiClient;
    private final de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.monitoring.ApiClient monitoringApiClient;
    private final de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.vorfaelleundvorkommnisse.ApiClient vorfaelleApiClient;
    private final de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.wahlvorbereitung.ApiClient wahlvorbereitungApiClient;

    @PostConstruct
    public void updateBasePaths() {
        basisdatenApiClient.setBasePath(basisdatenBasePath);
        briefwahlApiClient.setBasePath(briefwahlBasePath);
        broadcastApiClient.setBasePath(broadcastBasePath);
        infomanagementApiClient.setBasePath(infomanagementBasePath);
        monitoringApiClient.setBasePath(monitoringBasePath);
        vorfaelleApiClient.setBasePath(vorfaelleBasePath);
        wahlvorbereitungApiClient.setBasePath(wahlvorbereitungBasePath);
    }
}
