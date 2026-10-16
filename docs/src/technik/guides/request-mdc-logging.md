# Request MDC Logging Library

Diese Bibliothek aus `wls-common/monitoring` befüllt den MDC (Mapped Diagnostic Context) automatisch mit den Parameternamen und Werten (req.arg.*) eingehender Controller-Requests. Dank `WlsLoggingAutoConfiguration` ist die Einbindung in zwei Schritten erledigt.

## Dependency hinzufügen

Füge die Library `monitoring-x.y.z` in deiner pom.xml hinzu:

```xml
<dependency>
    <groupId>de.muenchen.oss.wahllokalsystem.wls-common</groupId>
    <artifactId>monitoring</artifactId>
    <version>x.y.z</version>
</dependency>
```

## Logger & MDC in logback-spring.xml konfigurieren

Die Steuerung des Aspekts und die Ausgabe der MDC-Werte erfolgen vollständig in deiner Logback-Konfiguration.
Füge das `<mdc/>`-Tag in den Appender ein und definiere den Logger für den Aspekt.

Beispiel:

```js {11,14}
<configuration>
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>
    <springProfile name="plainTextLogging">
        <!-- Log appender -->
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>
                    %date{yyyy.MM.dd HH:mm:ss.SSS} | ${springAppName} | TraceId: %X{traceId:-} | SpanId: %X{spanId:-}] | %level | [%thread] | %logger{0} | [%file : %line] - %msg%n
                </pattern>
            </encoder>
            <mdc/>
        </appender>
    </springProfile>
    <logger name="de.muenchen.oss.wahllokalsystem.wls.common.monitoring.logging" level="debug" />
</configuration>
```

## Log-Level Verhalten des Aspekts

* debug: Loggt nur den Methodeneingang. Der MDC bleibt aus Performance-Gründen leer.
* trace: Befüllt den MDC bei jedem erfolgreichen Request vollständig mit den Payloads.
* error: (Automatisch): Tritt im Controller ein Fehler auf, befüllt der Aspekt den MDC immer mit den Payloads und schreibt ein Error-Log – völlig unabhängig davon, welches Level oben im Logger definiert wurde.

::: info Technische Schutzmechanismen {important-note="true"}

* **Filterung:** Technische Klassen (`HttpServletRequest`, `Principal`, Spring Security `Authentication`, Mockito-Mocks) werden automatisch ignoriert.
* **Absturzsicher:** Zyklische Objektbeziehungen (z. B. JPA-Entitäten) werden bei der Serialisierung abgefangen und bringen den Request niemals zum Absturz.
:::
