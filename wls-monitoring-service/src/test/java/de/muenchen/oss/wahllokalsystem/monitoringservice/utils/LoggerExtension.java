package de.muenchen.oss.wahllokalsystem.monitoringservice.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

public class LoggerExtension implements BeforeEachCallback, AfterEachCallback {

  private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  private final List<Logger> attachedLoggers = new ArrayList<>();

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    listAppender.stop();
    listAppender.list.clear();
    for (Logger logger : attachedLoggers) {
      logger.detachAppender(listAppender);
    }
    attachedLoggers.clear();
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    Logger packageLogger =
        (Logger) LoggerFactory.getLogger(extensionContext.getRequiredTestClass().getPackageName());
    packageLogger.addAppender(listAppender);
    attachedLoggers.add(packageLogger);

    listAppender.start();
  }

  public Stream<ILoggingEvent> getLoggedEventsStream() {
    return listAppender.list.stream();
  }
}
