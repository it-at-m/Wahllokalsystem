package de.muenchen.wls.common.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Configuration
@ConditionalOnProperty(value = MonitoringProperties.PROPS_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "de.muenchen.wls.common.monitoring.health")
@EnableConfigurationProperties({ MonitoringProperties.class })
@Import(MetricsConfig.class)
public class MonitoringConfig extends WebSecurityConfigurerAdapter {

    public static final String CLIENT_REST = "wls.client.rest";

    @Autowired
    private MonitoringProperties monitoringProperties;

    /**
     * Disables security checks for metric and jolokia endpoint if enabled.
     *
     * @param web web security builder
     */
    @Override
    public void configure(WebSecurity web) {
        if (monitoringProperties.getJolokia().getEnabled()) {
            web.ignoring().antMatchers("/jolokia/**");
        }
        String prometheusEnabled = monitoringProperties.getMetrics().getPrometheus().getOrDefault("enabled", "false");
        if (Boolean.parseBoolean(prometheusEnabled)) {
            web.ignoring().antMatchers("/metrics");
        }
    }

}
