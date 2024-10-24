package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

public class LoggerExtension implements BeforeEachCallback, AfterEachCallback {

    private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        listAppender.stop();
        listAppender.list.clear();
        logger.detachAppender(listAppender);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        logger.addAppender(listAppender);
        listAppender.start();
    }

    public Stream<ILoggingEvent> getLoggedEventsStream() {
        return listAppender.list.stream();
    }
}
