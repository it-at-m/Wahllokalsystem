# Erstellung eines neue Microservices

Wenn ein neuer Microservice angelegt wird, sind dabei folgende Themen zu beachten.

## Maven-Projekt anlegen

Für den neuen Service wird ein Ordner parallel zu den anderen Service angelegt. Dabei auf das Namensschema achten:
`wls-<Domain>-service`

In dem Ordner wird das Maven-Projekt eingerichtet. Dazu aus den [RefArch-Templates](https://github.com/it-at-m/refarch-templates)
die Dateien des jeweiligen Unterordners in den erstellten Projektordner kopieren.

### Pflege der Dependencies und Plugins

Einen Überblick über die verwendeten Dependencies und Plugins geben die vorhandenen Services. Der Broadcast-Service ist
ein Service, der auf keine andere Services zugreift. Der Basisdaten-Service ist ein Service der auf andere Services
zugreift. Dementsprechend verwenden die Services unterschiedliche Plugins.

Da das RefArch-Template auf ein allgemeines Szenario abzielt, ist mit zusätzlichen Schritten zu rechnen, um den Service
funktionsfähig zu bekommen.

### Update der Tests

Im Projekte haben wir für das Naming unserer Tests [Konventionen](/technik/naming_conventions/testing) aufgestellt. Die bereitgestellten Tests des Templates
müssen entsprechend angepasst werden.

## Workflows einrichten

Im Repo gibt es diverse [Workflows](/technik/ecosystem/workflows). Die Workflows eines bestehenden Services sind zu
kopieren und die Trigger anzupassen.

> [!IMPORTANT]
> Beim Kopieren ist das [Namensschema](/technik/naming_conventions/workflows) zu beachten.

## Datenbank einrichten

Jeder Service bekommt einen eigenen Benutzer für die Datenbank. Die Zugriffs-URL ist für alle Services gleich:
`jdbc:oracle:thin:@//localhost:1521/XEPDB1`

Dabei sollte auf folgendes Schema geachtet werden:

- Benutzername: \<Name des Services mit Unterstrichen\>
- Passwort: secret

Beispiel für `wls-broadcast-service`:
- Benutzername: `wls_broadcast_service`
- Passwort: `secret`

# Routing im Gateway einrichten

Damit das Frontend mit dem Service kommunizieren kann ist im Gateway eine Route einzurichten. Das Routing erfolgt mit
dem Servicenamen.

Beispiel:

Anfragen die anden Broadcast-Service gehen sollen beginnen im Path mit `/api/broadcast-service/`.

# Pflege der Rechte im Auth-Service

Die Pflege der Rechte erfolgt in dem Auth-Service über Flyway-Files. Über `insert`-Statements werden die Rechte ergänzt
und die Zuordnung zu den Rollen vorgenommen.

> [!NOTE]
> Die Pflege der Rollen und Rechte erfolgt immer über neue, noch nicht im Default-Branch enthaltene, Flyway-Skripte.