package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceIDFormatter {

    private final String serviceName;

    public ServiceIDFormatter(@Value("${service.info.oid}") final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getId() {
        return serviceName;
    }
}
