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