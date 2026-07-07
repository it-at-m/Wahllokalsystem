# Backend-Microservice

Um einen neuen Backend-Service anzulegen sind zuvor die [allgemeinen Infos](/technik/guides/new-microservice/index.md)
zum Einrichten eines neuen Services zu beachten.

## Maven-Projekt anlegen

Für den neuen Service wird ein Ordner parallel zu den anderen Services angelegt. Dabei ist auf das Namensschema zu achten:
`wls-<Domain>-service`

In dem Ordner wird das Maven-Projekt eingerichtet. Dazu aus den [RefArch-Templates](https://github.com/it-at-m/refarch-templates/tree/main/refarch-backend/)
die Dateien des jeweiligen Unterordners in den erstellten Projektordner kopieren.

> [!NOTE]
>
> Damit die [IntelliJ-Runconfigurationen](https://it-at-m.github.io/Wahllokalsystem/technik/get_started/#runconfigurations)
> verwendbar sind, muss das Projekt importiert sein. Der Import
> erfolgt über das Kontextmenü der `pom.xml` > `+ Add as Maven Project`.

### Pflege der Dependencies und Plugins

Einen Überblick über die verwendeten Dependencies und Plugins geben die vorhandenen Services. Der Broadcast-Service ist
ein Service, der auf keine andere Services zugreift. Der Basisdaten-Service ist ein Service der auf andere Services
zugreift. Dementsprechend verwenden die Services unterschiedliche Plugins.

Da das RefArch-Template auf ein allgemeines Szenario abzielt, ist mit zusätzlichen Schritten zu rechnen, um den Service
funktionsfähig zu bekommen.

## Workflow Templates
<!-- prettier-ignore-start -->
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

```yml {1,6-7,16} [wls-&lt;domain&gt;-service_pull-request.yml]
name: verify pull request <domain>-service

on:
  pull_request:
    paths:
      - 'wls-<domain>-service/**'
      - '.github/workflows/wls-<domain>-service_pull-request.yml'

jobs:
  verify-pull-request:
    permissions: 
      contents: read
    uses:
      ./.github/workflows/callable-run-mvn-verify.yml
    with:
      pom-dir: 'wls-<domain>-service'
```

```yml {20} [dispatch-microservice-maven-release.yml]
name: dispatch microservice maven release

on:
  workflow_dispatch:
    inputs:
      release-version:
        required: true
        description: release version to build
      development-version:
        required: true
        description: next development version to set
      service:
        required: true
        type: choice
        description: service/directory to build (wls-broadcast-service, ...)
        options:
          - wls-auth-service
          - wls-basisdaten-service
          # - further services -
          - wls-<domain>-service
```

```yml {14} [dispatch-create-github-container-image.yml]
name: dispatch build github container image

on:
  workflow_dispatch:
    inputs:
      service:
        required: true
        type: choice
        description: service/directory to build (wls-broadcast-service, ...)
        options:
          - wls-auth-service
          - wls-basisdaten-service
          # - further services -
          - wls-<domain>-service
      tag:
        required: false
        description: 'optional: gittag'
```

:::

## Releases

Zusätzlich muss der neue Service im Workflow `populate-release-pr.yml`, sowie im passenden PR-Template ergänzt werden:
::: code-group

```yml {23,31} [.github/workflows/populate-release-pr.yml]
name: Populate Release PR Links

on:
  pull_request:
    types: [opened]

permissions:
  # ...

jobs:
  populate-links:
    # ...
    steps:
      - name: Update PR with Release Links
        uses: actions/github-script@v7
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          script: |
            const services = [
              'wls-admin-service',
              // further services,
              'wls-<domain>-service'
            ];

            // (... more code ... )
            
              const serviceHeadings = {
                'wls-admin-service': 'Admin-Service',
                // further services,
                'wls-<domain>-service': '<Domain>-Service
              };

            // (... more code ... )
```

```md {15-18,28-29} [.github/PULL_REQUEST_TEMPLATE/release-pr.md]
# Beschreibung:

Releases für den Sprint XX erstellt.
---

# Backend Services Releases

## Admin-Service

- [Release-Notes][wls-admin-service-release]
- [Image-Tag][wls-admin-service-image]

<!-- further services -->

## <Domain>-Service

- [Release-Notes][wls-<domain>-service-release]
- [Image-Tag][wls-<domain>-service-image]

---

<!-- Platzhalter für Release-Links - werden automatisch gefüllt -->

<!-- Backend Services -->
[wls-admin-service-release]: #
[wls-admin-service-image]: #
<!-- further services -->
[wls-<domain>-service-release]: #
[wls-<domain>-service-image]: #
```

:::
<!-- prettier-ignore-end -->
## Datenbank einrichten

Jeder Service hat ein eigenes Benutzerkonto für die Datenbank. Diese sind im File
`stack/oracle-database/add-user-on-startup.sql` hinterlegt. Die Zugriffs-URL ist für alle Services gleich:
`jdbc:oracle:thin:@//localhost:1521/XEPDB1`

Dabei sollte auf folgendes Schema geachtet werden:

- Benutzername: \<Name des Services mit Unterstrichen\>
- Passwort: secret

Beispiel für `wls-broadcast-service`:

- Benutzername: `wls_broadcast_service`
- Passwort: `secret`

## Routing im Gateway einrichten

Damit das Frontend mit dem Service kommunizieren kann, ist im Gateway eine neue Route einzurichten. Das Routing erfolgt mit
dem Servicenamen.

Beispiel:

Anfragen die an den Broadcast-Service gehen sollen beginnen im Path mit `/api/broadcast-service/`.

## Pflege der Rechte im Auth-Service

Die Pflege der Rechte erfolgt in dem Auth-Service über Flyway-Files. Über `insert`-Statements werden die Rechte ergänzt
und die Zuordnung zu den Rollen vorgenommen.

> [!NOTE]
> Die Pflege der Rollen und Rechte erfolgt immer über neue, noch nicht im Default-Branch enthaltene, Flyway-Skripte.

## Logging

Für ein einheitliches Logging über alle Services hinweg, sollte analog zu den bestehenden Microservices eine
`logback-spring.xml`-Datei erstellt werden.
