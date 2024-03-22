package de.muenchen.wls.common.monitoring;

import com.zaxxer.hikari.HikariDataSource;
import de.muenchen.wls.common.monitoring.metrics.binder.ConnectionPoolMetrics;
import de.muenchen.wls.common.monitoring.metrics.binder.HikariCpMetrics;
import de.muenchen.wls.common.monitoring.metrics.filter.HttpRequestMetricFilter;
import de.muenchen.wls.common.monitoring.metrics.registry.WlsMeterRegistry;
import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jpa.HibernateMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(value = MetricsProperties.PROPS_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
@EnableAspectJAutoProxy
@ComponentScan("de.muenchen.wls.common.monitoring.metrics.registry")
@EnableConfigurationProperties({ MetricsProperties.class })
class MetricsConfig {

    private static final Logger log = LoggerFactory.getLogger(MetricsConfig.class);
    private static final String HTTP_REQUEST_METRIC_NAME = "wls.http.request";

    @Autowired
    private MetricsProperties metricsProperties;

    @Autowired
    private ApplicationContext appContext;

    @Autowired(required = false)
    private EntityManagerFactory entityManagerFactory;

    @Autowired(required = false)
    private List<DataSourcePoolMetadataProvider> dataSourcePoolMetadataProvider;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private List<WlsMeterRegistry> wlsMeterRegistries = new ArrayList<>();

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Makes @Timed usable on any arbitrary method.
     *
     * @return micrometer time aspect
     */
    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect(meterRegistry());
    }

    @Bean
    @ConditionalOnProperty(value = MetricsProperties.PROPS_PREFIX + ".http-request-metrics", havingValue = "true")
    public FilterRegistrationBean httpRequestMetricsFilter() {
        log.debug("Initialize HttpRequestMetrics");
        HttpRequestMetricFilter filter = new HttpRequestMetricFilter(HTTP_REQUEST_METRIC_NAME, meterRegistry(), metricsProperties.getHttpRequestWhitelist(),
                metricsProperties.getHttpRequestBlacklist());
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setAsyncSupported(false);
        return registration;
    }

    /**
     * Creates a new micrometer registry and configures metric binders. See {@link MetricsProperties}.
     *
     * @return Instance of the created micrometer registry
     */
    @Bean
    public MeterRegistry meterRegistry() {
        Assert.notNull(applicationName, "Property 'spring.application.name' must not be null");

        CompositeMeterRegistry registry = new CompositeMeterRegistry();
        wlsMeterRegistries.forEach(r -> registry.add(r.getRegistry()));

        registry.config().commonTags("application", applicationName);

        metricsProperties.getTags().keySet().forEach(key -> registry.config().commonTags(key, metricsProperties.getTags().get(key)));

        // Add configured metrics
        if (metricsProperties.getOpenFilesLimitMetrics()) {
            addOpenFilesLimit(registry);
        }
        if (metricsProperties.getHikariCpMetrics()) {
            addHikariCp(registry);
        }
        if (metricsProperties.getHibernateMetrics()) {
            addHibernate(registry);
        }
        if (metricsProperties.getDataSourceMetrics()) {
            addDataSource(registry);
        }
        if (metricsProperties.getJvmMemoryMetrics()) {
            addJvmMemory(registry);
        }
        if (metricsProperties.getJvmThreadMetrics()) {
            addJvmThread(registry);
        }
        if (metricsProperties.getProcessorMetrics()) {
            addProcessor(registry);
        }
        if (metricsProperties.getProcessMemoryMetrics()) {
            addProcessMemory(registry);
        }
        if (metricsProperties.getProcessThreadMetrics()) {
            addProcessThread(registry);
        }
        if (metricsProperties.getTomcatMetrics()) {
            addTomcat(registry);
        }
        if (metricsProperties.getJvmGcMetrics()) {
            addJvmGc(registry);
        }
        if (metricsProperties.getUptimeMetrics()) {
            addUptime(registry);
        }
        if (metricsProperties.getLogbackMetrics()) {
            addLogback(registry);
        }
        if (metricsProperties.getHttpRequestMetrics() && metricsProperties.getHttpRequestHistogramEnabled()) {
            addHttpRequestHistogram(registry);
        }
        return registry;
    }

    private void addHikariCp(MeterRegistry registry) {
        if (dataSource != null && dataSource instanceof HikariDataSource) {
            log.debug("Initialize HikariCpMetrics");
            new HikariCpMetrics(((HikariDataSource) dataSource).getHikariPoolMXBean()).bindTo(registry);
        } else {
            log.warn("DataSource is not a HikariDataSource. HikariCpMetrics are disabled.");
        }
    }

    private void addDataSource(MeterRegistry registry) {
        if (dataSource != null && dataSourcePoolMetadataProvider != null) {
            log.debug("Initialize DataSourceMetrics");
            new ConnectionPoolMetrics(dataSource, dataSourcePoolMetadataProvider).bindTo(registry);
        } else {
            log.warn("DataSource is not configured. HibernateMetrics are disabled.");
        }
    }

    private void addHibernate(MeterRegistry registry) {
        if (entityManagerFactory != null && entityManagerFactory.unwrap(SessionFactory.class) != null) {
            log.debug("Initialize HibernateMetrics");
            new HibernateMetrics(entityManagerFactory.unwrap(SessionFactory.class), "hibernateFactory", new ArrayList<>()).bindTo(registry);
        } else {
            log.warn("Entity manager factory is not a hibernate factory. HibernateMetrics are disabled.");
        }
    }

    private void addProcessor(MeterRegistry registry) {
        log.debug("Initialize ProcessorMetrics");
        new ProcessorMetrics().bindTo(registry);
    }

    private void addProcessThread(MeterRegistry registry) {
        log.debug("Initialize ProcessThreadMetrics");
        new ProcessThreadMetrics().bindTo(registry);
    }

    private void addProcessMemory(MeterRegistry registry) {
        log.debug("Initialize ProcessMemoryMetrics");
        new ProcessMemoryMetrics().bindTo(registry);
    }

    private void addTomcat(MeterRegistry registry) {
        log.debug("Initialize TomcatMetrics");
        Manager manager = null;
        if (appContext instanceof ServletWebServerApplicationContext) {
            EmbeddedServletContainer container = ((ServletWebServerApplicationContext) appContext).getEmbeddedServletContainer();
            if (container instanceof TomcatEmbeddedServletContainer) {
                for (Container tom : ((TomcatEmbeddedServletContainer) container).getTomcat().getHost().findChildren()) {
                    if (tom instanceof Context) {
                        manager = ((Context) tom).getManager();
                        break;
                    }
                }
            }
        }
        new TomcatMetrics(manager, Collections.emptyList()).bindTo(registry);
    }

    private void addJvmMemory(MeterRegistry registry) {
        log.debug("Initialize JvmMemoryMetrics");
        new JvmMemoryMetrics().bindTo(registry);
    }

    private void addJvmThread(MeterRegistry registry) {
        log.debug("Initialize JvmThreadMetrics");
        new JvmThreadMetrics().bindTo(registry);
    }

    private void addOpenFilesLimit(MeterRegistry registry) {
        log.debug("Initialize OpenFilesLimitMetrics");
        new FileDescriptorMetrics().bindTo(registry);
    }

    private void addUptime(MeterRegistry registry) {
        log.debug("Initialize UptimeMetrics");
        new UptimeMetrics().bindTo(registry);
    }

    private void addLogback(MeterRegistry registry) {
        log.debug("Initialize LogbackMetrics");
        new LogbackMetrics().bindTo(registry);
    }

    private void addJvmGc(MeterRegistry registry) {
        log.debug("Initialize JvmGcMetrics");
        new JvmGcMetrics().bindTo(registry);
    }

    private void addHttpRequestHistogram(MeterRegistry registry) {
        log.debug("Activate histogram for HttpRequestMetrics");
        registry.config().meterFilter(
                new MeterFilter() {
                    @Override
                    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                        if (id.getName().startsWith(HTTP_REQUEST_METRIC_NAME)) {
                            return DistributionStatisticConfig.builder()
                                    .percentilesHistogram(true)
                                    .build()
                                    .merge(config);
                        }
                        return config;
                    }
                });
    }

}
