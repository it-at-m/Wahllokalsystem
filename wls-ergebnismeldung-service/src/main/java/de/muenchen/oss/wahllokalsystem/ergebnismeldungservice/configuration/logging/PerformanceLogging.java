package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PerformanceLogging {

    private static final String PERFORMANCE_LOGGER_PREFIX = "performance.";

    public static Logger createPerformanceLogger(String loggerName) {
        return LoggerFactory.getLogger(PERFORMANCE_LOGGER_PREFIX + loggerName);
    }
}
