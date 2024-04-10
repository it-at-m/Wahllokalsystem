# Kein Update von org.projectlombok:lombok durch renovate

## Status

<adr-status status='proposed'></adr-status>

## Kontext

Wir verwenden [Renovate](https://docs.renovatebot.com) um unsere Dependencies auf den aktuellen Stand zu halten. Renovate
erzeugt automatisch Pull-Requests mit Vorschlägen zu Anpassungen. Wir reviewen diese Vorschläge und nehmen diese entsprechend an.
Das Dashboard mit einer Übersicht was alles verwaltet wird ist in einem [Issue](https://github.com/it-at-m/Wahllokalsystem/issues/1).

In unseren Services verwenden wir `lombok`. Die Dependency dafür kommt von Spring als managed dependency:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <!-- Import dependency management from Spring Boot -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring.boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Zur korrekten Funktionsweise muss auch ein Annotation-Processor [eingerichtet](https://projectlombok.org/setup/maven) werden.

Die Version des Annotation-Processors muss in unserem maven projekt definiert werden:

```xml
<properties>
    <org.projectlombok.lombok.version>1.18.30</org.projectlombok.lombok.version>
</properties>
```

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${maven.compiler.plugin.version}</version>
    <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${org.projectlombok.lombok.version}</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-mapstruct-binding</artifactId>
                <version>${org.projectlombok.mapstructbinding.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

Die Versionen der Dependency und vom Processor sollten gleich sein. Die Wahrscheinlichkeit für Probleme wird mit dem Unterschied der Version (Major, Minor, Patch) zusammenhängen. 
Da die Versionen aber an unterschiedlicher Stelle gepflegt werden kann dies aktuell nicht
technisch sichergestellt werden. Daher wurde ein Kommentar in der `pom.xml` in den `properties` eingefügt:
```xml
<properties>
    <!-- Version muss mit der in den spring-boot-dependencies bereitgestellten Lombok-Version übereinstimmen -->
    <org.projectlombok.lombok.version>1.18.30</org.projectlombok.lombok.version>
</properties>
```

Renovate kann diesen Kommentar natürlich nicht beachten und erstellt bei entsprechenden möglichen Updates Pull-Requests.
Diese werden wir aktuell schließen. Da das Wahllokalsystem aus knapp ca. 15 Services am Ende bestehen wird, würden so regelmäßig
zahlreiche PRs entstehen, die wir schließen müssten. 

Updates von `lombok` machen wir bei Updates von `spring` mit.

## Entscheidung

Wir lassen `renovate` `org.projectlombok:lombok` ignorieren. Updates von `lombok` erfolgen wie gehabt manuell.

### betrachtete Alternative

Es wäre möglich die Pflege der Version im `annotationProcessorPaths` weg zu bekommen. Dazu müsste man an Stelle von
`annotationProcessorPaths` [`annotationProcessors` pflegen](https://projectlombok.org/contributing/lombok-execution-path) und dort die expliziten Klassen referenzieren. Kommt Mapstruct,
hinzu muss `org.mapstruct:mapstruct-processor` als Dependency vorhanden sein.

Aus meiner Sicht spricht gegen dieses Vorgehen, dass die Processors manuell gepflegt werden müssen, was Aufgrund der Kleinteiligkeit
mühsamer sein dürfte als mit `annotationProcessorPaths` zu arbeiten. Bei Hebungen der
Version müssten wir prüfen, ob sich etwas an der Architektur geändert hat, z.B. ob es jetzt eine weitere Klasse gibt, die man
verwenden müsste. Klassen die gelöscht werden sorgen für Fehler und somit für schnelles Feedback.

Beide Konfigurationsmöglichkeiten können nicht zur selben Zeit verwendet werden.

## Konsequenzen

### positiv

Weniger Pull-Requests die gehandhabt werden müssen.

### negativ

Steigerung der Komplexität durch eine zusätzliche Konfiguration für eine Dependency. Ein Verweis in der Konfiguration auf diesen ADR ist nicht möglich.

`lombok` wird in keinem Projekt mehr durch `renovate` gepflegt. Das halte ich für vertrebar da wir `lombok` immer im Kontext von `spring` verwenden.