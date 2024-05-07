# Verbesserung der Einstiegsfreundlichkeit durch neue Default-Werte

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Wenn wir OpenSource auf Github entwickeln und somit anderen Personen die Möglichkeit geben unsere Anwendung zu verwenden,
sollten wir dafür sorgen, dass die Einstiegshürde so gering wie möglich ist.

Aktuell ist nicht standardisiert welche Datenbank wann verwendet wird. Abhängig von dem beim Start verwendeten Profil
kann entweder eine H2- oder Oracle-DB verwendet werden. Während H2 ohne zusätzliche Infrastruktur in Memory läuft
benötigt Oracle einen Datenbankserver.

Des Weiteren ist aktuell das Profil `local` notwendig um die Services zu starten. Die in den Services hinterlegten Startskripte
sind daher wichtig, um die Services zu starten.

Wenn man mehrere Services lokal starten möchte, muss man eigenständig auf die Ports achten und diese gegenfalls anpassen. 

## Entscheidung

Ohne zusätzliche Konfiguration soll beim Start eines Service per Default die H2-InMemory-Datenbank verwendet werden. Oracle kann
durch zusätzliche Konfiguration verwendet werden.

Durch technische Profile kann die Art der Datenbank geändert werden.

Zum Start muss kein explizites Profil angegeben werden. Ein simple `java -jar Microservice.jar` soll ausreichend sein.

Jeder Microservice bekommt einen eigenen Port per Default, beginnend bei `39146` für den Broadcast-Service und dann weiter
mit `39147` für den Briefwahlservice, `39148` für den Infomanagement-Service und `39149` für den EAI-Service. Weitere Services
reihen sich entsprechend nachfolgend ein.

## Konsequenzen

Die bereits erstellen Services müssen angepasst werden.

### positiv

Leichterer Einstieg für Dritte in die Anwendung weil nun ein Service ohne Anpassung der Konfiguration und zusätzliche Infrastruktur direkt gestartet werden kann .

### negativ

Bei der Erstellung eines Services müssen weitere Regeln beachtet werden. Services die miteinander Kommunizieren müssen
aufeinander abgestimmt werden (z.B. beim Port). 