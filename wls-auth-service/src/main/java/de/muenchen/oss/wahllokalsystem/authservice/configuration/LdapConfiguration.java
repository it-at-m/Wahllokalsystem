package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.properties.ServiceAuthLdapProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ldap.EmbeddedLdapServerContextSourceFactoryBean;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LdapConfiguration {

    private final ServiceAuthLdapProperties serviceAuthLdapProperties;

    @Bean
    AuthenticationManager authenticationManager(final BaseLdapPathContextSource contextSource) {
        val factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserSearchFilter(serviceAuthLdapProperties.getUserSearchFilter());
        factory.setUserSearchBase(serviceAuthLdapProperties.getUserSearchBase());
        return factory.createAuthenticationManager();
    }

    @Bean
    @Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
    public LdapContextSource ldapContextSource() {
        val contextSourceValue = new DefaultSpringSecurityContextSource(serviceAuthLdapProperties.getContextSource());
        contextSourceValue.setAnonymousReadOnly(serviceAuthLdapProperties.isAnonymousReadOnly());
        contextSourceValue.setCacheEnvironmentProperties(serviceAuthLdapProperties.isCacheEnvironmentProperties());
        val userDn = serviceAuthLdapProperties.getUserDn();
        if (userDn != null) {
            contextSourceValue.setUserDn(userDn);
            contextSourceValue.setPassword(serviceAuthLdapProperties.getUserDnPassword());
        }
        contextSourceValue.afterPropertiesSet();
        return contextSourceValue;
    }

    @Bean
    @Profile(Profiles.DUMMY_CLIENTS)
    public EmbeddedLdapServerContextSourceFactoryBean embeddedLdapContextSource() {
        log.warn("using embeddedLdapContextSource");
        return EmbeddedLdapServerContextSourceFactoryBean.fromEmbeddedLdapServer();
    }

}
