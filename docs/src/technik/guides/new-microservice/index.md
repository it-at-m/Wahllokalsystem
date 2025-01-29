# Erstellung eines neue Microservices

Wenn ein neuer Microservice angelegt wird, sind dabei einige Themen zu beachten. 

## Update der Tests

Im Projekt haben wir für das Naming unserer Tests [Konventionen](/technik/naming_conventions/testing) aufgestellt. Die bereitgestellten Tests der 
[Refarch-Templates](https://github.com/it-at-m/refarch-templates/), welches im Projekt eingesetzt wird, müssen 
entsprechend angepasst werden.

## Workflows einrichten
<!-- Anmerkung: Zeilen 14-18 werden in anderen Files importiert. Falls sich durch Anpassungen die Zeilen ändern-->

Im Repo gibt es diverse [Workflows](/technik/ecosystem/workflows). Die Workflows eines bestehenden Services sind zu
kopieren und die Trigger anzupassen.

> [!IMPORTANT]
> Beim Kopieren ist das [Namensschema](/technik/naming_conventions/workflows) zu beachten. Außerdem muss zwingend für 
> ein Frontend-Projekt ein Frontend-Workflow und für einen Backend-Service ein Backend-Workflow dupliziert werden!

## Individuelle Anpassungen 

Einige Einstellungen sind davon abhängig, ob es sich um einen Frontend oder Backend-Service handelt. Die folgenden 
Guides sollen dabei unterstützen:

1. Guide zur Erstellung eines [neuen Backend-Services](/technik/guides/new-microservice/new-service-backend.md)
2. Guide zur Erstellung eines [neuen Frontend-Projekts](/technik/guides/new-microservice/new-service-frontend.md)

