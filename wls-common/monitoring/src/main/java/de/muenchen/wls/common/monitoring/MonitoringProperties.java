package de.muenchen.wls.common.monitoring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = MonitoringProperties.PROPS_PREFIX)
public class MonitoringProperties {

    public static final String PROPS_PREFIX = "wls.monitoring";

    private Boolean enabled = Boolean.TRUE;

    private JolokiaProperties jolokia = new JolokiaProperties();

    @NestedConfigurationProperty
    private MetricsProperties metrics;

    public JolokiaProperties getJolokia() {
        return jolokia;
    }

    public void setJolokia(JolokiaProperties jolokia) {
        this.jolokia = jolokia;
    }

    @Data
    public static class JolokiaProperties {

        private Boolean enabled = Boolean.FALSE;

    }

}
