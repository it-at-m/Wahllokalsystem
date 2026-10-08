package de.muenchen.oss.wahllokalsystem.wls.common.monitoring.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
    classes = {
      RequestMdcLoggingAspectTest.TestConfig.class,
      RequestMdcLoggingAspectTest.DummyController.class
    })
class RequestMdcLoggingAspectTest {

  @Autowired private DummyController dummyController;

  private ListAppender<ILoggingEvent> listAppender;
  private Logger aspectLogger;
  private Level originalLogLevel;

  public record TestDto(String id, String name) {}

  @RestController
  static class DummyController {
    // Simuliert einen Standard-Request mit fachlichen Query-Parametern und einem DTO-Body
    public String requestWithFachlichParametersAndBody(
        @PathVariable("param") String param, @RequestBody TestDto body) {
      return "success";
    }

    // Simuliert einen Request, der neben fachlichen Daten auch technische Framework-Klassen
    // injiziert bekommt
    public String requestContainingTechnicalFrameworkClasses(
        @PathVariable("param") String param, HttpServletRequest request, Principal principal) {
      return "success-tech";
    }

    // Simuliert einen Request-Absturz im Controller
    public void requestThatThrowsException(@PathVariable("param") String param) {
      throw new RuntimeException("Simulierter Absturz am Wahlabend");
    }
  }

  @Configuration
  @EnableAspectJAutoProxy
  static class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public RequestMdcLoggingAspect requestMdcLoggingAspect(ObjectMapper objectMapper) {
      return new RequestMdcLoggingAspect(objectMapper);
    }
  }

  @BeforeEach
  void setup() {
    MDC.clear();

    // Logback-Appender anhängen, um emittierte Logs im Test zu validieren
    aspectLogger = (Logger) LoggerFactory.getLogger(RequestMdcLoggingAspect.class);
    originalLogLevel = aspectLogger.getLevel();

    listAppender = new ListAppender<>();
    listAppender.start();
    aspectLogger.addAppender(listAppender);
  }

  @AfterEach
  void teardown() {
    MDC.clear();
    aspectLogger.detachAppender(listAppender);
    aspectLogger.setLevel(originalLogLevel);
  }

  @Test
  void should_logOnlyDebugMessage_when_levelIsDebug() {
    aspectLogger.setLevel(Level.DEBUG);

    dummyController.requestWithFachlichParametersAndBody("wert1", new TestDto("123", "Wahllokal"));

    // Überprüfung: DEBUG Logzeile vorhanden
    assertThat(listAppender.list).hasSize(1);
    assertThat(listAppender.list.get(0).getLevel()).isEqualTo(Level.DEBUG);
    assertThat(listAppender.list.get(0).getFormattedMessage())
        .contains(
            "Eingehender Request in Controller: DummyController -> requestWithFachlichParametersAndBody()");

    // Überprüfung: MDC blieb leer (Performance-Schutz greift)
    assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
  }

  @Test
  void should_fillMdcWithArguments_when_levelIsTrace() {
    aspectLogger.setLevel(Level.TRACE);

    dummyController.requestWithFachlichParametersAndBody("wert1", new TestDto("123", "Wahllokal"));

    // Der MDC wird im 'finally'-Block geleert, wir prüfen die temporäre Befüllung
    // über die erfassten Log-Events, da der Logstash-Encoder dort den MDC ausliest.
    assertThat(listAppender.list).hasSize(2); // 1x DEBUG (Eingang), 1x TRACE (Erfolgsmeldung)

    ILoggingEvent traceEvent =
        listAppender.list.stream()
            .filter(e -> e.getLevel() == Level.TRACE)
            .findFirst()
            .orElseThrow();

    Map<String, String> mdcContext = traceEvent.getMDCPropertyMap();
    assertThat(mdcContext.get("req.arg.param")).isEqualTo("wert1");
    assertThat(mdcContext.get("req.arg.body"))
        .contains("\"id\":\"123\"")
        .contains("\"name\":\"Wahllokal\"");
  }

  @Test
  void should_ignoreTechnicalClassesFromMdc_when_runningInTraceMode() {
    aspectLogger.setLevel(Level.TRACE);

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    Principal mockPrincipal = mock(Principal.class);

    dummyController.requestContainingTechnicalFrameworkClasses("wert1", mockRequest, mockPrincipal);

    ILoggingEvent traceEvent =
        listAppender.list.stream()
            .filter(e -> e.getLevel() == Level.TRACE)
            .findFirst()
            .orElseThrow();

    Map<String, String> mdcContext = traceEvent.getMDCPropertyMap();

    // Fachliche Parameter müssen da sein
    assertThat(mdcContext.get("req.arg.param")).isEqualTo("wert1");
    // Technische Klassen dürfen NICHT im MDC auftauchen (Blacklist-Schutz)
    assertThat(mdcContext.containsKey("req.arg.request")).isFalse();
    assertThat(mdcContext.containsKey("req.arg.principal")).isFalse();
  }

  @Test
  void should_alwaysLogErrorWithPayloads_when_exceptionIsThrownEvenIfLevelIsInfo() {
    // Aspekt steht auf INFO -> Im Normalbetrieb stumm
    aspectLogger.setLevel(Level.INFO);

    assertThatThrownBy(() -> dummyController.requestThatThrowsException("kritischerWert"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Simulierter Absturz am Wahlabend");

    // Wahlabend-Sicherheit: Trotz INFO muss ein ERROR-Log geschrieben worden sein
    assertThat(listAppender.list).hasSize(1);
    ILoggingEvent errorEvent = listAppender.list.get(0);
    assertThat(errorEvent.getLevel()).isEqualTo(Level.ERROR);
    assertThat(errorEvent.getFormattedMessage())
        .contains("Fehler bei der Verarbeitung im Controller");

    // WICHTIG: Die Payloads müssen im MDC des Error-Logs verankert sein
    Map<String, String> mdcContext = errorEvent.getMDCPropertyMap();
    assertThat(mdcContext.get("req.arg.param")).isEqualTo("kritischerWert");
  }

  @Test
  void should_preserveAndRestoreExistingMdcContext_when_aspectExecutionFinished() {
    aspectLogger.setLevel(Level.TRACE);

    // Kontext simulieren (z. B. UserMdcFilter hat den User bereits gesetzt)
    MDC.put("req.user", "wahlvorstand_user");
    MDC.put("traceId", "abcdef123");

    dummyController.requestWithFachlichParametersAndBody("wert1", new TestDto("1", "X"));

    // Nach dem Request muss der MDC exakt restauriert sein (Verhinderung von Memory Leaks)
    assertThat(MDC.get("req.user")).isEqualTo("wahlvorstand_user");
    assertThat(MDC.get("traceId")).isEqualTo("abcdef123");
    // Aspekt-Keys müssen rückstandslos entfernt worden sein
    assertThat(MDC.get("req.arg.param")).isNull();
  }
}
