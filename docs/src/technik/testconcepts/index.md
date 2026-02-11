# Testkonzept

Auf den Seiten zum [Backend](backend) und Frontend werden die jeweiligen Testkonzepte beschrieben.

Für den Aufbau und die Benennung von Tests sind die [Namingkonventions](../naming_conventions/) zu beachten.

Unabhängig von Backend und Frontend gibt es weitere manuelle Teststufen.

## Manuelle Teststufen

### Code-Review

Ziel: Überprüfung der Code-Qualität und der korrekten Umsetzung der Anforderungen, sowie die Einhaltung von Projektvorgaben.

Was: Jeder Pull-Request, der auf den Default-Branch geht, muss gereviewt werden. Pull-Requests mit einem anderen Ziel
können optional einem Review unterzogen werden.

Wie: Die Workflows stellen eine syntaktische Funktionsfähigkeit der Anwendung fest, sowie die Einhaltung eines definierten
Stils. Reviewer\*innen prüfen die Einhaltung der Definition of Done, sowie eine korrekte, vollständige und effiziente
Umsetzung. In der Regel muss für das Review der Code lokal ausgecheckt werden. Feedback ist im Pull-Request zu geben.

Verantwortung: Entwicklungsteam

Ausführung: Der Ersteller oder die Erstellerin eines Pull-Requests trägt die Verantwortung, dass ein Review
durchgeführt wird. Entwickler\*innen aus dem Entwicklungsteam weisen sich selbständig PRs für ein Review zu.

### Sprint-Review

Ziel: Überprüfung, ob die Anforderungen, die in einem Issue gestellt wurden, fachlich korrekt umgesetzt wurden.

Was: Jedes Issue, dass eine für den Fachbereich relevante Änderung hatte.

Wie: Vorstellung des Ergebnisses im Rahmen der Demo. In Vorbereitung auf die Demo wird geklärt, durch wen die Vorstellung
erfolgt.

Verantwortung: Entwicklungsteam

Ausführung: Am Ende jedes Sprints auf der Umgebung für die Demo.

## Bedarfsgesteuerte Tests

Zusätzlich zu den beschriebenen Tests werden je nach Bedarf folgende weitere Tests durchgeführt:

- Systemtests, technisch und fachlich
- Last und Performancetests
- Pentests
