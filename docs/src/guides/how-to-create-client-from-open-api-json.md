# API-Client aus OpenAPI-Spec erstellen

## Einleitung

Alle Services stellen ihre API in Form einer OpenAPI Spezifikation zur Verfügung. Diese Dateien liegen den Assets
der jeweiligen Releases bei.

![Übersicht über Release von wls-eai-service Version 0.0.1-RC1](/screenshot-wls-eai-service-release-0.0.1-RC1.png)  
*Übersicht über [Release](https://github.com/it-at-m/Wahllokalsystem/releases/tag/wls-eai-service%2F0.0.1-RC1) von
wls-eai-service Version 0.0.1-RC1*

Durch ein Maven-Plugin lassen sich aus der Datei `openapi.json` der Client generieren.

## Einfügen des Plugins

Um aus der Spezifikation die Java-Klassen zu erstellen, muss das Generator-Plugin eingebunden werden.

```xml

<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.5.0</version>
    <executions>
    </executions>
</plugin>
```

Für jeden zu erzeugenden Client mus ein `execution`-Element definiert werden.

## Konfigurieren der Ausführung

```xml

<execution>
    <!-- wenn mehrere Clients erzeugt werden muss jede execution eine eindeutige ID haben -->
    <id>generateEAI</id>
    <goals>
        <goal>generate</goal>
    </goals>
    <configuration>
        <!-- Pfad zur JSON-Datei mit der OpenAPI-Beschreibung -->
        <inputSpec>${project.basedir}/src/main/resources/openapis/openapi.eai.0.0.1-RC1.json</inputSpec>

        <!-- Wir wollen Java-Klassen generieren-->
        <generatorName>java</generatorName>
        <!-- Als Client wird das Resttemplate verwendet-->
        <library>resttemplate</library>

        <!-- Package in dem die Clients der jeweiligen Controller erzeugt werden -->
        <!-- Hier wird das Package für Zugriff auf den EAI-Service innerhalb des Basisdatenservices definiert -->
        <apiPackage>${groupId}.basisdatenservice.eai.aou.client</apiPackage>
        <!-- Package in das die Datenklassen erzeugt werden -->
        <modelPackage>${groupId}.basisdatenservice.eai.aou.model</modelPackage>

        <!-- Weitere Klassen als Grundlagen für zusätzliche Tests sind nicht erforderlich-->
        <generateApiTests>false</generateApiTests>
        <generateModelTests>false</generateModelTests>
        <!-- Die zusätzliche Generierung der Dokumentation ist nicht erforderlich.
        Das JavaDoc der Klassen enthält bereits die Dokumentation -->
        <generateApiDocumentation>false</generateApiDocumentation>
        <generateModelDocumentation>false</generateModelDocumentation>

        <configOptions>
            <!-- Fügt einen Zeitstempel mit dem Zeitpunkt der Generierung bei den Klassen ein -->
            <hideGenerationTimestamp>false</hideGenerationTimestamp>
            <!-- Empfohlen laut Doku -->
            <legacyDiscriminatorBehavior>false</legacyDiscriminatorBehavior>
            <!-- der Client wird mit @Component annotiert -->
            <generateClientAsBean>true</generateClientAsBean>
            <!-- damit da jakarta-Package verwendet wird -->
            <useJakartaEe>true</useJakartaEe>
        </configOptions>

        <globalProperties>
            <!-- zusätzliche Klassen, die notwendig sind damit die Clients korrekt arbeiten können -->
            <supportingFiles>
                BaseApi.java,ApiClient.java,JavaTimeFormatter.java,Authentication.java,OAuth.java,ApiKeyAuth.java,HttpBasicAuth.java,HttpBearerAuth.java,RFC3339DateFormat.java
            </supportingFiles>
        </globalProperties>
    </configuration>
</execution>
```

Eine ausführliche Beschreibung alle Konfigurationsoptionen gibt es in der
[offiziellen Dokumentation](https://openapi-generator.tech/docs/generators/java/).

Bei der Konfiguration für die Packages soll folgendes Schema beachtet werden:  
`<package von Microserviceapplication>.clients.<service>.(api|model)`

## Ergänzen von Dependencies

Damit die generierten Klassen compiliert werden können, muss folgende Dependency ergänzt werden:

```xml

<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.6</version>
</dependency>
```

Des Weiteren wird für die abschließende Konfiguration der Beans `wls-common:exception` benötigt:

```xml

<dependency>
    <groupId>de.muenchen.oss.wahllokalsystem.wls-common</groupId>
    <artifactId>exception</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Context um Beans erweitern

Damit der generierte Client verwendet werden kann, muss dem Spring-Context noch ein `RestTemplate` hinzugefügt werden:

```java

@Configuration
public class ClientConfiguration {

    @Bean
    public RestTemplate restTemplate(final WlsResponseErrorHandler wlsResponseErrorHandler) {
        val restTemplate = new RestTemplate();

        /* definieren des Errorhandlers für Antworten vom externen Service */
        restTemplate.setErrorHandler(wlsResponseErrorHandler);
        /* Ergänzen eines Interceptors um den Bearer-Token an den nächsten Service weiter zu geben */
        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return execution.execute(request, body);
            }

            if (!(authentication.getCredentials() instanceof AbstractOAuth2Token)) {
                return execution.execute(request, body);
            }

            val token = (AbstractOAuth2Token) authentication.getCredentials();
            request.getHeaders().setBearerAuth(token.getTokenValue());
            return execution.execute(request, body);
        });

        return restTemplate;
    }

}
```

Wie zu erkennen ist, wird für das RestTemplate ein ErrorHandler benötigt, wobei wir hier den `WlsResponseErrorHandler`
aus `wls-common:exception` verwenden. Eine entsprechende Bean muss ebenfalls noch dem Spring-Context hinzugefügt werden:

```java

@Bean
public WlsResponseErrorHandler wlsResponseErrorHandler(final ObjectMapper objectMapper) {
    return new WlsResponseErrorHandler(objectMapper);
}
```

## Target des Clients definieren

Nachdem jetzt alle Beans im Kontext sind, fehlt nur noch die Konfiguration unter welche Adresse der Service, für den wir
den Client generiert haben, erreichbar ist.

❗Diese Konfiguration darf nicht in derselben Konfigurationsklasse enthalten sein, in der das `RestTemplate` erstellt
wird, das sonst eine zirkulare Abhängigkeit vorliegt und die Anwendung nicht startet.

```java

@Configuration
@RequiredArgsConstructor
public class BasePathConfiguration {

    /* Umgebungsvariable welche die Ziel-URL enthält, z.b. http//localhost:39146 */
    @Value("${app.clients.eai.basePath}")
    String eaiBasePath;

    private final ApiClient eaiApiClient;

    @PostConstruct
    public void updateBasePaths() {
        eaiApiClient.setBasePath(eaiBasePath);
    }
}
```

In der `application.yml` wird der Defaultwert für die Ziel-URL hinterlegt:

```yml
app:
  clients:
    eai:
      basePath: http://localhost:39146
```

## EAI-Client definieren und verwenden

Abschließend wird der EAI-Client, welche das Datenmodell des aufgerufenen externen Services auf das Datenmodell unseres
Services mappt, erstellt:

```java

@Component
@RequiredArgsConstructor
public class DemoClient {

    private final WahlvorschlagControllerApi wahlvorschlagControllerApi;

    public DemoDTO getDemo() {
        val wahlvorschlaege = wahlvorschlagControllerApi.loadWahlvorschlaege("wahlID", "wahlbezirkID");

        return new DemoDTO(wahlvorschlaege.getStimmzettelgebietID(), "" + wahlvorschlaege.getWahlvorschlaege().size());
    }

}
```