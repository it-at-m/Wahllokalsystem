# Backend-Microservice

Um einen neuen Backend-Service anzulegen sind zuvor die [allgemeinen Infos](/technik/guides/new-microservice) zum 
Einrichten eines neuen Services zu beachten.

### Maven-Projekt anlegen

Für den neuen Service wird ein Ordner parallel zu den anderen Services angelegt. Dabei ist auf das Namensschema zu achten:
`wls-<Domain>-service`

In dem Ordner wird das Maven-Projekt eingerichtet. Dazu aus den [RefArch-Templates](https://github.com/it-at-m/refarch-templates/tree/main/refarch-backend/)
die Dateien des jeweiligen Unterordners in den erstellten Projektordner kopieren.

#### Pflege der Dependencies und Plugins

Einen Überblick über die verwendeten Dependencies und Plugins geben die vorhandenen Services. Der Broadcast-Service ist
ein Service, der auf keine andere Services zugreift. Der Basisdaten-Service ist ein Service der auf andere Services
zugreift. Dementsprechend verwenden die Services unterschiedliche Plugins.

Da das RefArch-Template auf ein allgemeines Szenario abzielt, ist mit zusätzlichen Schritten zu rechnen, um den Service
funktionsfähig zu bekommen.

### Workflow Templates

::: code-group
```yml {1,8-9,18} [wls-&lt;domain&gt;-service_push-dev.yml]
name: build push dev <domain>-service

on:
  push:
    branches:
      - dev
    paths:
      - 'wls-<domain>-service/**'
      - '.github/workflows/wls-<domain>-service_push-dev.yml'

jobs:
  build-github-container-image:
    permissions:
      packages: write
    uses:
      ./.github/workflows/callable-create-github-container-image.yml
    with:
      service: 'wls-<domain>-service'
```

```yml {1,6-7,14} [wls-&lt;domain&gt;-service_pull-request.yml]
name: verify pull request <domain>-service

on:
  pull_request:
    paths:
      - 'wls-<domain>-service/**'
      - '.github/workflows/wls-<domain>-service_pull-request.yml'

jobs:
  verify-pull-request:
    uses:
      ./.github/workflows/callable-run-mvn-verify.yml
    with:
      pom-dir: 'wls-<domain>-service'
``` 
:::

### Datenbank einrichten

Jeder Service hat einen eigenen Benutzer für die Datenbank. Diese sind im File `stack/oracle-database/add-user-on-startup.sql` hinterlegt. Die Zugriffs-URL ist für alle Services gleich:
`jdbc:oracle:thin:@//localhost:1521/XEPDB1`

Dabei sollte auf folgendes Schema geachtet werden:

- Benutzername: \<Name des Services mit Unterstrichen\>
- Passwort: secret

Beispiel für `wls-broadcast-service`:
- Benutzername: `wls_broadcast_service`
- Passwort: `secret`

### Routing im Gateway einrichten

Damit das Frontend mit dem Service kommunizieren kann, ist im Gateway eine neue Route einzurichten. Das Routing erfolgt mit
dem Servicenamen.

Beispiel:

Anfragen die an den Broadcast-Service gehen sollen beginnen im Path mit `/api/broadcast-service/`.

### Pflege der Rechte im Auth-Service

Die Pflege der Rechte erfolgt in dem Auth-Service über Flyway-Files. Über `insert`-Statements werden die Rechte ergänzt
und die Zuordnung zu den Rollen vorgenommen.

> [!NOTE]
> Die Pflege der Rollen und Rechte erfolgt immer über neue, noch nicht im Default-Branch enthaltene, Flyway-Skripte.

## Frontend-Microservice

🚧 -> https://github.com/it-at-m/Wahllokalsystem/issues/742