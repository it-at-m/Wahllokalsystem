package de.muenchen.oss.wahllokalsystem.authservice.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "service.config.ldap", ignoreUnknownFields = true)
@Data
public class ServiceAuthLdapProperties {

    private String userDn;
    private String userDnPassword;
    private String contextSource;
    private String userSearchBase;
    private String userSearchFilter;
    private boolean anonymousReadOnly = false;
    private boolean cacheEnvironmentProperties = false;
}
