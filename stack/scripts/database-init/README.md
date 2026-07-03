# database-init

## Beschreibung

Wir möchten mithilfe eines Shell-Skriptes, alle erfassten Daten von Nutzern einer Umgebung zurücksetzen.

## Aufbau

Das Skript ließt über einen Ordner die Services und die dafür notwendige Flyway-Konfiguration. In einem zweiten Ordner liegen die Flyway-Skripte die für den Service ausgeführt werden soll.
Die Flyway-Skripte für die Migration sind Repeatable Migrations damit man sie auch ohne Änderung an dem File erneut ausführen kann um erfasste Daten zu löschen.

Die Namen der Unterordner mit den Flyway-Konfigurationen müssen identisch sein zu den Namen der Unterordner für die Flyway-Skripte.

## Anleitung

1. in diesen Ornder mit dem Skript wechsel4
2. dort `./run-migrations.sh <umgebungsname> [<Skriptordner>]`

### Parameterbeschreibung

### Umgebungsname

Beschreibt den Namen des Ordners, in dem die Flyway-Konfigurationen liegen. Die Angabe muss relativ zum Skript sein. In der Regel wird es local sein.

### Skriptordner

Ist ein optionaler Wert. Default ist `_scripts`. Damit wird der Ordner beschrieben, in dem die Flyway-Skripte liegen die je Service ausgeführt werden.

## Vorgehen im Fehlerfall

Falls das Skript für einen Service fehlschlägt, wird im `flyway_database_init_history`-File in der DB des Services ein 
Eintrag mit `success=0` angelegt. Durch die `validateOnMigrate`-Konfiguration wird im Anschluss jeder weitere 
Versuch, das Skript neu auszuführen ebenfalls fehlschlagen. Daher muss der entsprechende Eintrag im 
`flyway_database_init_history`-File manuell entfernt werden.