package de.muenchen.wls.common.monitoring.metrics.registry;

import de.muenchen.wls.common.monitoring.MetricsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
@ConditionalOnProperty(name = MetricsProperties.PROPS_PREFIX + ".prometheus.enabled", havingValue = "true")
public class RegistryResource {

    private WlsPrometheusRegistry prometheus;

    @Autowired
    public RegistryResource(WlsPrometheusRegistry prometheus) {
        this.prometheus = prometheus;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getMetrics() {
        return prometheus.getRegistry().scrape();
    }

}
