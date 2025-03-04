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

```yml {1,8-9,18} [wls-gui-<frontend-name>_push-dev.yml]
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

```yml {1,6-7,14} [wls-gui-<frontend-name>_pull-request.yml]
name: verify pull request gui <frontend-name>

on:
  pull_request:
    paths:
      - "wls-gui-<frontend-name>/**"
      - ".github/workflows/wls-gui-<frontend-name>_pull-request.yml"

jobs:
  verify-pull-request:
    uses: ./.github/workflows/callable-run-npm-build.yml
    with:
      package-dir: "wls-gui-<frontend-name>"
```

:::

## Routing im Gateway einrichten

Damit der Port und die URL für das neue Frontend-Projekt korrekt verknüpft wird, muss das
[`application-routes.yml`-File](https://github.com/it-at-m/Wahllokalsystem/blob/dev/stack/gateway_config/application-routes.yml)
entsprechend angepasst werden:

```yaml
spring:
  cloud:
    gateway:
      routes:
        # ...
        - id: gui-<frontend-name> // [!code focus:4]
          uri: http://kubernetes.docker.internal:<PORT>/
          predicates:
            - Path=/<frontend-name>/**
```

> [!IMPORTANT]
> Es wird immer die erste Route verwendet, welche die Bedingungen (predicates) erfüllt. Nur `Path=/**` wäre auf alle Pfade
> anwendbar, weshalb alle Routen, die danach noch kommen, nicht mehr berücksichtigt werden. Daher muss eine Route mit
> dem Pfad `Path=/**` immer an letzter Stelle stehen.

## Ungenutzte Refarch-Elemente entfernen

Damit der Code sauber und übersichtlich bleibt, sollten die Elemente des Refarch-Templates, die nicht für das Projekt
benötigt werden, wie zum Beispiel [Mucatar](https://github.com/it-at-m/Wahllokalsystem/pull/661/files) entfernt werden.
