# Backendtestkonzept

## Automatisierte Teststufen

Die folgenden Teststufen werden durch die Workflows automatisiert durchlaufen. Bevor ein Pull-Requests gemergt wird,
müssen alle Teststufen erfolgreich durchlaufen sein.

Die Tests werden mittels JUnit5 und AssertJ geschrieben.

Die [Architekturtests](#architekturtests) sind die einzigen Tests, die bei einer Implementierung eines Features
nicht aktualisiert werden.

### Unittests

Ziel: Sicherstellen der fachlichen Funktionalität einer Klasse.

Was: Neue oder geänderte Funktionalität einer Klasse.

Wie: Erstellen, Anpassen oder Erweitern vorhandener Testsuits. Es sollte bei eigenen Klassen eine Codecoverage
von 100% (Methoden und Lines) erreicht werden. Komponenten, auf die die Testunit zugreifen, werden gemockt.

Verantwortung: Entwickler\*in, der/die die Änderung implementiert hat.

### Integrationstests zur Authentifizierung

Ziel: Sicherstellen, dass der Zugriff auf Endpunkte der Services mit der
erforderlichen [Authentifizierung](../systemspecification/backend#authentifizierung) erfolgt.

Was: Geänderte Authentifizierungsregeln, neue oder geänderte Endpunkte eines Services.

Wie: Durch Anpassung der `configuration.SecurityConfigurationTest`-Testsuit.
Das Verhalten mit und ohne korrekter Authentifizierung ist zu prüfen.
Die korrekte Fachlichkeit des Zugriffes soll dabei nicht geprüft werden.

Verantwortung: Entwickler\*in, der/die die Änderung implementiert hat.

### Integrationstests zur Autorisierung

Ziel: Sicherstellen, dass der Zugriff auf die Operationen eines Services oder Repositories
nur mit den erforderlichen [Berechtigungen](../systemspecification/backend#autorisierung) erfolgt.

Was:

- Neue oder geänderte Berechtigungen, von Methoden bei Services oder Repositories.
- Neue oder geänderte Berechtigungen, aufgrund von neuen oder geänderten Abhängigkeiten.

Wie: Durch Anpassungen in `service.<Domain>SecurityTest`. Der erfolgreiche Zugriff, sowie die Zugriffsverweigerung
sind zu prüfen.
Gibt es mehrere erforderliche Berechtigungen, müssen alle Kombinationen geprüft werden.
Die korrekte logische Verknüpfung der Berechtigungen muss dabei beachtet werden.
Die korrekte Fachlichkeit des Zugriffes soll dabei nicht geprüft werden.

Verantwortung: Entwickler\*in, der/die die Änderung implementiert hat.

### Integrationstest des Microservices unter Beachtung der Fachlichkeit

Ziel: Sicherstellen, dass die grundlegenden Einsatzmöglichkeiten eines Microservices gewährleistet sind, in dem
die Komponenten des Microservices korrekt zusammenarbeiten.

Was: Stellvertretende positive wie negative Testfälle einer fachlichen Anforderung eines Services. Überprüfung
des korrekten Umgangs mit Exceptions.

Wie: Durch Anpassungen in `rest.<Domain>ControllerIntegrationTest`, welche Zugriff auf die Endpunkte unter Verwendung der
jeweiligen Authentifizierung durchführen.
Eine vollständige Abdeckung aller fachlichen Szenarien ist nicht erforderlich, weil dies durch die
[Unittests](#unittests) der Services sichergestellt wird.

Verantwortung: Entwickler\*in, der/die die elementare Änderung an der Fachlichkeit implementiert hat.

### Architekturtests

Ziel: Sicherstellen, dass formalisierte Regeln der Architektur eingehalten wurden.

Was: Die Regeln, die für den Service als relevant definiert wurden.

Wie: Durch Anpassungen in `ArchUnitTest`, unter Verwendung von Regeln aus `wls-common` und speziell für den Service
erstellte Regeln. Bei der Implementierung von Anforderungen sind in der Regel keine Anpassungen an den Architekturtests
vorzunehmen. Mit den Architekturtests wird aber sichergestellt, dass die Implementierung sich an die definierten Regeln hält.

Verantwortung: Entwickler\*in, der/die das entsprechende Issue zur Pflege der Architekturtests umsetzt,
oder bei Änderungen an der Architektur.

> [!NOTE]
> Serviceunabhängige Regeln werden im Modul `wls-common:testing` gepflegt. Gruppiert nach dem Testgegenstand
> der Regel, gibt es im Package `archunit.rule` Klassen mit Konstanten zu Regeln für Klassen und Methoden.
