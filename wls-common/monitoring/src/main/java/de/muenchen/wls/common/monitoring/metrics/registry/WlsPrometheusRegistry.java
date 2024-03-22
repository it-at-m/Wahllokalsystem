package de.muenchen.wls.common.monitoring.metrics.registry;

import de.muenchen.wls.common.monitoring.MetricsProperties;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusRenameFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = MetricsProperties.PROPS_PREFIX + ".prometheus.enabled", havingValue = "true")
public class WlsPrometheusRegistry implements WlsMeterRegistry {

    private PrometheusMeterRegistry registry;

    @Autowired
    public WlsPrometheusRegistry(MetricsProperties metricsProperties) {
        Map<String, String> prometheusProps = metricsProperties.getPrometheus();
        registry = new PrometheusMeterRegistry(new WlsPrometheusConfig(prometheusProps));
        registry.config().meterFilter(new PrometheusRenameFilter());
    }

    @Override
    public PrometheusMeterRegistry getRegistry() {
        return registry;
    }

}
