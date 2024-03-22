package de.muenchen.wls.common.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = MetricsProperties.PROPS_PREFIX)
public class MetricsProperties {

    public static final String PROPS_PREFIX = "wls.monitoring.metrics";

    private Boolean enabled = Boolean.TRUE;

    private Map<String, String> influx = new HashMap<>();

    private Map<String, String> prometheus = new HashMap<>();

    private String environment;

    private Boolean hibernateMetrics = Boolean.FALSE;

    private Boolean jvmMemoryMetrics = Boolean.FALSE;

    private Boolean jvmThreadMetrics = Boolean.FALSE;

    private Boolean hikariCpMetrics = Boolean.FALSE;

    private Boolean dataSourceMetrics = Boolean.FALSE;

    private Boolean openFilesLimitMetrics = Boolean.FALSE;

    private Boolean processorMetrics = Boolean.FALSE;

    private Boolean processThreadMetrics = Boolean.FALSE;

    private Boolean processMemoryMetrics = Boolean.FALSE;

    private Boolean tomcatMetrics = Boolean.FALSE;

    private Boolean jvmGcMetrics = Boolean.FALSE;

    private Boolean uptimeMetrics = Boolean.FALSE;

    private Boolean logbackMetrics = Boolean.FALSE;

    private Boolean httpRequestMetrics = Boolean.FALSE;

    private List<String> httpRequestBlacklist = new ArrayList<>();

    private List<String> httpRequestWhitelist = new ArrayList<>();

    private Boolean httpRequestHistogramEnabled = Boolean.TRUE;

    private Map<String, String> tags = new HashMap<>();

    public Map<String, String> getPrometheus() {
        return prometheus;
    }

    public void setPrometheus(Map<String, String> prometheus) {
        this.prometheus = prometheus;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getInflux() {
        return influx;
    }

    public void setInflux(Map<String, String> influx) {
        this.influx = influx;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

}
