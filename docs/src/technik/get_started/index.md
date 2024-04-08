# Getting started

## Formatter einrichten

Im Projekte verwenden wir `checkstyle` und `spotless` um für einen möglichst einheitlichen Codestyle zu sorgen.
Dazu haben wir Regeln definiert. Diese Regeln und deren Hinterlegung in der jeweiligen IDE ist
[hier](https://github.com/it-at-m/itm-java-codeformat) beschrieben.

## Workflows

### Pull-Requests

Mit der Erstellung eines Pull-Requests wird mittel Workflow geprüft das Code funktional ist: `mvn verify`. Das bedeutet
es werden die Tests ausgeführt und geprüft dass das Codestyle den Anforderungen entspricht.

### Push auf Defaultbranch

Unser Defaultbranch ist `dev`. Mit einem Push auf den Branch wird bei den Backendservices ein Containerimages erzeugt
das in der Github-Container-Registriy hinterlegt wird. Als Tag wird `latest-dev` verwendet.

### Namenskonventionen

❗ Ist zu beachten dass alle Workflows im Ordner `workflows` liegen müssen. Sie dürfen nicht auf unterordner aufgeteilt werden.
Zur besseren Strukturierung in dem Ordner soll daher auf eine einheitliche Namenskonvention geachtet werden.

- `callable-<kurzbeschreibung>.yml` ... [wiederverenbare Workflows](https://docs.github.com/de/actions/using-workflows/reusing-workflows)
- `dispatch-<kurzbeschreibung>.yml` ... [manuell ausführbare workflows](https://docs.github.com/de/actions/using-workflows/manually-running-a-workflow)
- `<service-namer>_<trigger>.yml` ... Workflows zu Jobs die durch ein bestimmtes Ereignis getriggert werden.

#### Beispiele

##### `callable-<kurzbezeichnung>.yml`

`callable-create-github-container-image.yml` beinhaltet einen wiedervenbaren Workflow der ein Containerimage für Github erstellt.

##### `dispatch-<kurzbeschreibung>.yml`

`dispatch-microservice-mvn-release.yml` ist ein Workflow der manuell getriggert wird ein Maven-Release eines Mikroservices durchzuführen.

##### `<service-namer>_<trigger>.yml`

`wls-broadcast-service_push-dev-yml` ist der Workflow zum `wls-broadcast-service` der bei einem `push` auf `dev` ausgeführt wird.

`doc_pull-request.yml` ist der Workflow zur Dokumentation, der bei einem `pull request` ausgeführt wird.