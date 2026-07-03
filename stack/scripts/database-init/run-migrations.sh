#!/bin/bash

# Speichere das ursprüngliche Verzeichnis
PWD=$(pwd)
echo "PWD: $PWD"
SCRIPT_PATH="$(dirname "$(realpath "$0")")"

# Überprüfen, ob die Umgebung bereits als Argument übergeben wurde
if [ -z "$1" ]; then
    # Benutzer nach der Umgebung fragen
    echo "Bitte geben Sie die Umgebung ein (wls3-c, wls3-k, usw): "
    read ENVIRONMENT
else
    ENVIRONMENT="$1"
fi

PATH_TO_SCRIPTS="_scripts"
# Gibt es ein expliziten Pfad zu den Skripten?
if [ -n "$2" ]; then
    # Benutzer nach der Umgebung fragen
    PATH_TO_SCRIPTS="$2"
fi
echo "Ablageort der Skripte: $PATH_TO_SCRIPTS"

# Überprüfen, ob das Verzeichnis für die angegebene Umgebung existiert
BASE_DIR="$PWD/$ENVIRONMENT"
if [ ! -d "$BASE_DIR" ]; then
    echo "Die Umgebung '$ENVIRONMENT' existiert nicht. Bitte überprüfen Sie den Namen."
    exit 1
fi

# Durchlaufe alle Service-Verzeichnisse in der angegebenen Umgebung
for SERVICE_DIR in "$BASE_DIR"/*; do
    if [ -d "$SERVICE_DIR" ]; then
        SERVICE_NAME=$(basename "$SERVICE_DIR")
        # Pfad zur flyway.conf-Datei
        flywayConfigFiles="$ENVIRONMENT/$SERVICE_NAME/flyway.conf"
        flywayLocations="filesystem:$PATH_TO_SCRIPTS/$SERVICE_NAME"
        echo "Parameter to run with:"
        echo "flywayConfigFiles: $flywayConfigFiles"
        echo "flywayLocations: $flywayLocations"

        # Überprüfen, ob die flyway.conf existiert
        if [ ! -f "$flywayConfigFiles" ]; then
            echo "Die Datei 'flyway.conf' existiert nicht in $flywayConfigFiles."
            continue
        fi

        echo "Führe Flyway-Migrate für ${SERVICE_DIR} aus..."

        # Flyway-Befehl ausführen

        mvn flyway:migrate -Dflyway.configFiles="$flywayConfigFiles" -Dflyway.locations="$flywayLocations"

        if [ $? -ne 0 ]; then
            echo "Fehler beim Ausführen von Flyway für ${SERVICE_DIR}"
        else
            echo "Flyway erfolgreich für ${SERVICE_DIR} ausgeführt."
        fi
    fi
done

