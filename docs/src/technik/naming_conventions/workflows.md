# Naming Convetions für Workflows

> [!CAUTION]
> Es ist zu beachten, dass alle Workflows im Ordner `workflows` liegen müssen. Sie dürfen nicht auf unterordner aufgeteilt werden.
> Zur besseren Strukturierung in dem Ordner soll daher auf eine einheitliche Namenskonvention geachtet werden.

- `callable-<kurzbeschreibung>.yml` ... [wiederverwendbare Workflows](https://docs.github.com/de/actions/using-workflows/reusing-workflows)
- `dispatch-<kurzbeschreibung>.yml` ... [manuell ausführbare workflows](https://docs.github.com/de/actions/using-workflows/manually-running-a-workflow)
- `<service-name>_<trigger>.yml` ... Workflows zu Jobs die durch ein bestimmtes Ereignis getriggert werden.

## Beispiele

### `callable-<kurzbezeichnung>.yml`

`callable-create-github-container-image.yml` beinhaltet einen wiederverwendbaren Workflow der ein Containerimage für Github erstellt.

### `dispatch-<kurzbeschreibung>.yml`

`dispatch-microservice-mvn-release.yml` ist ein Workflow, der manuell getriggert wird um ein Maven-Release eines Mikroservices durchzuführen.

### `<service-name>_<trigger>.yml`

`wls-broadcast-service_push-dev-yml` ist der Workflow zum `wls-broadcast-service` der bei einem `push` auf `dev` ausgeführt wird.

`doc_pull-request.yml` ist der Workflow zur Dokumentation, der bei einem `pull request` ausgeführt wird.