# Frontend-Projekt

Um ein neues Frontend-Projekt anzulegen sind zuvor die [allgemeinen Infos](/technik/guides/new-microservice/index.md)
zum Einrichten eines neuen Services zu beachten.

## Referenzarchitektur-Template klonen

Um ein neues Frontend-Projekt anzulegen, wird das entsprechende Frontend-Template der
[Referenzarchitektur](https://github.com/it-at-m/refarch-templates/tree/main/refarch-frontend) benötigt. Da wir einen
neuen Frontend-Service brauchen, ist nur der Ordner `refarch-frontend` des Templates relevant.

Der einfachste Weg, diesen in das WLS-Projekt zu integrieren, ist es, das Refarch-Repository zu klonen und anschließend
den `refarch-frontend`-Ordner zu kopieren und lokal im Projekt einzufügen. Dafür wird der Ordner umbenannt, weil er
dem Namensschema `wls-gui-<frontend-name>` entsprechen sollte.

::: details Beispiel
Zum Beispiel heißt der Ordner `wls-gui-wahllokalsystem` für das Wahllokalsystem und könnte `wls-gui-admintool` für das
Admintool bezeichnet werden.
:::

## Workflow Templates

::: code-group

```yml {1,8-9,17} [wls-gui-&ltfrontend-name&gt_push-dev.yml]
name: build push dev gui <frontend-name>

on:
  push:
    branches:
      - dev
    paths:
      - "wls-gui-<frontend-name>/**"
      - ".github/workflows/wls-gui-<frontend-name>_push-dev.yml"

jobs:
  build-github-container-image:
    permissions:
      packages: write
    uses: ./.github/workflows/callable-create-github-container-image-frontend.yml
    with:
      service: "wls-gui-<frontend-name>"
```

```yml {1,6-7,15} [wls-gui-&ltfrontend-name&gt_pull-request.yml]
name: verify pull request gui <frontend-name>

on:
  pull_request:
    paths:
      - "wls-gui-<frontend-name>/**"
      - ".github/workflows/wls-gui-<frontend-name>_pull-request.yml"

jobs:
  verify-pull-request:
    permissions:
      contents: read
    uses: ./.github/workflows/callable-run-npm-build.yml
    with:
      package-dir: "wls-gui-<frontend-name>"
```

```yml {13} [dispatch-create-github-container-image-frontend.yml]
name: dispatch build github container image for frontend

on:
  workflow_dispatch:
    inputs:
      service:
        required: true
        type: choice
        description: frontend-service/directory to build (wls-gui-wahllokalsystem, ...)
        options:
          - wls-gui-wahllokalsystem
          - wls-gui-admintool
          - wls-gui-<domain>
      tag:
        required: false
        description: 'optional: gittag'
```

:::

## Releases

Zusätzlich muss die neue GUI im Workflow `populate-release-pr.yml`, sowie im passenden PR-Template ergänzt werden:
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
              // further services,
              'wls-gui-wahllokalsystem',
              'wls-gui-<frontend-name>'
            ];

            // (... more code ... )
            
              const serviceHeadings = {
                // further services,
                'wls-gui-wahllokalsystem': 'Wahllokalsystem GUI',
                'wls-gui-<frontend-name>': '<frontend-name> GUI'
              };

            // (... more code ... )
```

```md {17-20,30-31} [.github/PULL_REQUEST_TEMPLATE/release-pr.md]
# Beschreibung:

Releases für den Sprint XX erstellt.
---
<!-- backend services -->

# Frontend GUI Releases

<!-- further frontend guis -->

## Wahllokalsystem GUI

- [Release-Notes][wls-gui-wahllokalsystem-release]
- [Image-Tag][wls-gui-wahllokalsystem-image]


## <frontend-name> GUI

- [Release-Notes][wls-gui-<frontend-name>-release]
- [Image-Tag][wls-gui-<frontend-name>-image]
---

<!-- Platzhalter für Release-Links - werden automatisch gefüllt -->

<!-- backend Services -->
<!-- Frontend GUIs -->
<!-- further guis -->
[wls-gui-wahllokalsystem-release]: #
[wls-gui-wahllokalsystem-image]: #
[wls-gui-<frontend-name>-release]: #
[wls-gui-<frontend-name>-image]: #
```

:::

## Routing im Gateway einrichten

Damit der Port und die URL für das neue Frontend-Projekt korrekt verknüpft wird, muss das
[`application-routes.yml`-File](https://github.com/it-at-m/Wahllokalsystem/blob/dev/stack/gateway_config_wls/application-routes.yml)
entsprechend angepasst werden:

```yaml
spring:
  cloud:
    gateway:
      routes:
        # ...
        - id: gui-<frontend-name> # [!code focus:4]
          uri: http://host.docker.internal:<PORT>/
          predicates:
            - Path=/<frontend-name>/**
```

> [!IMPORTANT]
> Es wird immer die erste Route verwendet, welche die Bedingungen (predicates) erfüllt. Nur `Path=/**` wäre auf alle Pfade
> anwendbar, weshalb alle Routen, die danach noch kommen, nicht mehr berücksichtigt werden. Daher muss eine Route mit
> dem Pfad `Path=/**` immer an letzter Stelle stehen.

## Ungenutzte Refarch-Elemente entfernen

Folgende Elemente aus den Refarch-Templates können entfernt werden:

- formatter.ts
- Snackbar.ts und TheSnackbar.vue
- SaveLeave.ts
- Files zu Mucatar

Im [Ticket](https://github.com/it-at-m/Wahllokalsystem/issues/903) wurde ein Frontend-Projekt, inklusive entfernen aller ungenutzten Elemente erstellt.
