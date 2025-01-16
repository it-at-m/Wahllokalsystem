# Erstellung eines neue Microservices

## Maven-Projekt anlegen

## Workflows einrichten

## Datenbank einrichten

Jeder Service bekommt einen eigenen Benutzer für die Datenbank. Die Zugriffs-URL ist für alle Services gleich:
`jdbc:oracle:thin:@//localhost:1521/XEPDB1`

Neben dem Standardbenutzer der auf alles zugreifen kann (siehe `docker-compose.yml`) müssen alle weiteren Benutzer über
`stack/add-user-on-startup.sql` erstellt werden.

Dabei sollte auf folgendes Schema geachtet werden:

- Benutzername: \<Name des Services mit Unterstrichen\>
- Passwort: secret

Beispiel für `wls-broadcast-service`:
- Benutzername: `wls_broadcast_service`
- Passwort: `secret`

# Routing im Gateway einrichten

# Pflege der Rechte im Auth-Service