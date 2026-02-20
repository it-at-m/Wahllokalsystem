# Admin-Service

Der Service dient als Fassade für die admin-gui für den Zugriff auf andere Microservices.

## Abhängigkeiten

Der Service nutzt folgende Services

- Basisdaten
- Ergebnismeldung
- Infomanagement
- Auth

## Daten und Funktionen

- Verwaltung der Benutzer
- Festlegung des Wahltages
- Verwaltung der Wahltermindaten

## Architekturabweichungen

Dieser Service hat keinen [Persistencelayer](../../../technik/systemspecification/backend#backendservices#persistencelayer). Der Service spricht ausschließlich mit anderen Microservices.
