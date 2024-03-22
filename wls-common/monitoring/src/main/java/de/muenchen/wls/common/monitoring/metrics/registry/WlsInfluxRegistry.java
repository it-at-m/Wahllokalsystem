package de.muenchen.wls.common.monitoring.metrics.registry;

import de.muenchen.wls.common.monitoring.MetricsProperties;
import io.micrometer.core.instrument.Clock;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = MetricsProperties.PROPS_PREFIX + ".influx.enabled", havingValue = "true")
public class WlsInfluxRegistry implements WlsMeterRegistry {

    private InfluxMeterRegistry registry;

    @Autowired
    public WlsInfluxRegistry(MetricsProperties metricsProperties) {
        Map<String, String> influxProps = metricsProperties.getInflux();
        InfluxConfig config = new WlsInfluxConfig(influxProps);
        registry = new InfluxMeterRegistry(config, Clock.SYSTEM);
    }

    @Override
    public InfluxMeterRegistry getRegistry() {
        return registry;
    }

}
