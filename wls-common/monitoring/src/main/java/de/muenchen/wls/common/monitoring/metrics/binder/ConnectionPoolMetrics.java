package de.muenchen.wls.common.monitoring.metrics.binder;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;

public class ConnectionPoolMetrics implements MeterBinder {

    private static final String METRIC_NAME_PREFIX = "datasource.pool";
    private static final String METRIC_NAME_ACTIVE = METRIC_NAME_PREFIX + ".connections.active";
    private static final String METRIC_NAME_MAX = METRIC_NAME_PREFIX + ".connections.max";
    private static final String METRIC_NAME_MIN = METRIC_NAME_PREFIX + ".connections.min";
    private static final String METRIC_NAME_USAGE = METRIC_NAME_PREFIX + ".connections.usage";

    private DataSource dataSource;
    private DataSourcePoolMetadata providerMeta;

    public ConnectionPoolMetrics(DataSource dataSource, List<DataSourcePoolMetadataProvider> dataSourcePoolMetadataProvider) {
        DataSourcePoolMetadataProvider provider = new DataSourcePoolMetadataProvider(dataSourcePoolMetadataProvider);
        this.providerMeta = provider.getDataSourcePoolMetadata(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public void bindTo(MeterRegistry registry) {

        Gauge.builder(METRIC_NAME_ACTIVE, dataSource, ds -> nullSafeValue(providerMeta.getActive()))
                .description("Number of active connections that have been allocated")
                .register(registry);

        Gauge.builder(METRIC_NAME_MAX, dataSource, ds -> providerMeta.getMax())
                .description("Maximum number of active connections")
                .register(registry);

        Gauge.builder(METRIC_NAME_MIN, dataSource, ds -> providerMeta.getMin())
                .description("Minimum number of idle connections")
                .register(registry);

        Gauge.builder(METRIC_NAME_USAGE, dataSource, ds -> nullSafeValue(providerMeta.getUsage()))
                .description("Pool usage (Between 0 and 1)")
                .register(registry);

    }

    private Integer nullSafeValue(Integer integerVal) {
        return integerVal != null ? integerVal : 0;
    }

    private Float nullSafeValue(Float floatVal) {
        return floatVal != null ? floatVal : 0.0F;
    }

}
