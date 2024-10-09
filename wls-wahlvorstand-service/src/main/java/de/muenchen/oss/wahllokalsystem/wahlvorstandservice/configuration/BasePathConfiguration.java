package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration;

import org.springframework.beans.factory.annotation.Value;

public class BasePathConfiguration {

    @Value("${app.clients.eai.basePath}")
    String eaiBasePath;

    @Value("${app.clients.infomanagement.basePath}")
    String infomanagementBasePath;

    @Value("${app.clients.basisdaten.basePath}")
    String basisdatenBasePath;


    private final de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.ApiClient eaiApiClient;
    private final de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.ApiClient infomanagementApiClient;
    private final de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.basisdaten.ApiClient basisdatenApiClient;

}
