package de.muenchen.wls.common.monitoring.metrics.registry;

import io.micrometer.prometheus.PrometheusConfig;

import java.time.Duration;
import java.util.Map;

public class WlsPrometheusConfig implements PrometheusConfig {

    private Map<String, String> prometheusProps;

    public WlsPrometheusConfig(Map<String, String> prometheusProps) {
        this.prometheusProps = prometheusProps;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            return null;
        }
        return prometheusProps.get(key.replace(PrometheusConfig.DEFAULT.prefix() + ".", ""));
    }

    public Duration step() {
        String v = this.get(this.prefix() + ".step");
        return v == null ? Duration.ofSeconds(30L) : Duration.parse(v);
    }

}
