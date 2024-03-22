package de.muenchen.wls.common.monitoring.metrics.registry;

import io.micrometer.core.instrument.MeterRegistry;

public interface WlsMeterRegistry {

    MeterRegistry getRegistry();

}
