package de.muenchen.wls.common.monitoring.metrics.registry;

import io.micrometer.influx.InfluxConfig;

import java.util.Map;

public class WlsInfluxConfig implements InfluxConfig {

    private Map<String, String> influxProps;

    public WlsInfluxConfig(Map<String, String> influxProps) {
        this.influxProps = influxProps;
    }

    @Override
    public String get(String key) {
        if(key == null) {
            return null;
        }
        return influxProps.get(key.replace(InfluxConfig.DEFAULT.prefix() + ".", ""));
    }

}
