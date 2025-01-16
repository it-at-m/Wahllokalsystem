# Workflows

## Pull-Requests

Mit der Erstellung eines Pull-Requests wird mittels Workflow geprüft das der Code funktional ist: `mvn verify`. Das bedeutet
es werden die Tests ausgeführt und geprüft, dass der Codestyle den Anforderungen entspricht.

## Push auf Defaultbranch

Unser Defaultbranch ist `dev`. Mit einem Push auf den Branch wird bei den Backendservices ein Containerimages erzeugt
das in der Github-Container-Registry hinterlegt wird. Als Tag wird `latest-dev` verwendet.