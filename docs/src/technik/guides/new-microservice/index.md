# Erstellung eines neue Microservices

Wenn ein neuer Microservice angelegt wird, sind dabei einige Themen zu beachten.

## Update der Tests

Im Projekt haben wir für das Naming unserer Tests [Konventionen](/technik/naming_conventions/tests) aufgestellt. Die bereitgestellten Tests der
[Refarch-Templates](https://github.com/it-at-m/refarch-templates/), welches im Projekt eingesetzt wird, müssen
entsprechend angepasst werden.

## Workflows einrichten

Im Repo gibt es diverse [Workflows](/technik/ecosystem/workflowsAndArtifacts/). Die Workflows eines bestehenden Services sind zu
kopieren und die Trigger anzupassen.

> [!IMPORTANT]
> Beim Kopieren ist das [Namensschema](/technik/naming_conventions/workflows) zu beachten. Außerdem muss zwingend für
> ein Frontend-Projekt ein Frontend-Workflow und für einen Backend-Service ein Backend-Workflow dupliziert werden!

Zusätzlich muss der neue Service im Workflow `populate-release-pr.yml`, sowie im passenden PR-Template ergänzt werden:
::: code-group

```yml {24,33} [.github/workflows/populate-release-pr.yml]
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
              'wls-auth-service',
              // further services,
              'wls-<domain>-service'
            ];

            // (... more code ... )
            
              const serviceHeadings = {
                'wls-admin-service': 'Admin-Service',
                'wls-auth-service': 'Auth-Service',
                // further services,
                'wls-<domain>-service': '<Domain>-Service
              };

            // (... more code ... )
```

```md {18-21,31,32} [.github/PULL_REQUEST_TEMPLATE/release-pr.md]
# Beschreibung:

Releases für den Sprint XX erstellt.
---

## Admin-Service

- [Release-Notes][wls-admin-service-release]
- [Image-Tag][wls-admin-service-image]

## Auth-Service

- [Release-Notes][wls-auth-service-release]
- [Image-Tag][wls-auth-service-image]

<!-- further services -->

## <Domain>-Service

- [Release-Notes][wls-<domain>-service-release]
- [Image-Tag][wls-<domain>-service-image]

<!-- Platzhalter für Release-Links - werden automatisch gefüllt -->

<!-- Backend Services -->
[wls-admin-service-release]: #
[wls-admin-service-image]: #
[wls-auth-service-release]: #
[wls-auth-service-image]: #
<!-- further services -->
[wls-<domain>-service-release]: #
[wls-<domain>-service-image]: #
```

:::
## Individuelle Anpassungen

Einige Einstellungen sind davon abhängig, ob es sich um einen Frontend oder Backend-Service handelt. Die folgenden
Guides sollen dabei unterstützen:

1. Guide zur Erstellung eines [neuen Backend-Services](/technik/guides/new-microservice/new-service-backend.md)
2. Guide zur Erstellung eines [neuen Frontend-Projekts](/technik/guides/new-microservice/new-service-frontend.md)
