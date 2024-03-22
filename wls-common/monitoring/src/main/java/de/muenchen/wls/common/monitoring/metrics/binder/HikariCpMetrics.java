package de.muenchen.wls.common.monitoring.metrics.binder;

import com.zaxxer.hikari.HikariPoolMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

public class HikariCpMetrics implements MeterBinder {

    private static final String METRIC_NAME_PREFIX = "datasource.hikaricp";
    private static final String METRIC_NAME_ACTIVE = METRIC_NAME_PREFIX + ".connections.active";
    private static final String METRIC_NAME_TOTAL = METRIC_NAME_PREFIX + ".connections.total";
    private static final String METRIC_NAME_IDLE = METRIC_NAME_PREFIX + ".connections.idle";
    private static final String METRIC_NAME_PENDING = METRIC_NAME_PREFIX + ".connections.pending";

    private HikariPoolMXBean poolBean;

    public HikariCpMetrics(HikariPoolMXBean poolBean) {
        this.poolBean = poolBean;
    }

    @Override
    public void bindTo(MeterRegistry registry) {

        Gauge.builder(METRIC_NAME_TOTAL, poolBean, HikariPoolMXBean::getTotalConnections)
                .description("Total connections")
                .register(registry);

        Gauge.builder(METRIC_NAME_IDLE, poolBean, HikariPoolMXBean::getIdleConnections)
                .description("Idle connections")
                .register(registry);

        Gauge.builder(METRIC_NAME_ACTIVE, poolBean, HikariPoolMXBean::getActiveConnections)
                .description("Active connections")
                .register(registry);

        Gauge.builder(METRIC_NAME_PENDING, poolBean, HikariPoolMXBean::getThreadsAwaitingConnection)
                .description("Pending threads")
                .register(registry);

    }

}
