# database-init

## Beschreibung

Wir möchten mithilfe eines Shell-Skripts alle erfassten Daten von Nutzern einer Umgebung zurücksetzen.

## Aufbau

Das Skript liest über einen Ordner die Services und die dafür notwendige Flyway-Konfiguration aus. In einem zweiten Ordner liegen die Flyway-Skripte, die für den jeweiligen Service ausgeführt werden sollen.  
Die Flyway-Skripte für die Migration sind Repeatable Migrations, damit sie auch ohne Änderungen an den Dateien erneut ausgeführt werden können, um erfasste Daten zu löschen.

Die Namen der Unterordner mit den Flyway-Konfigurationen müssen identisch sein mit den Namen der Unterordner für die Flyway-Skripte.

## Anleitung

1. In diesen Ordner mit dem Skript wechseln.
2. Dort `./run-migrations.sh local [<Skriptordner>]` ausführen.

### Parameterbeschreibung

#### Umgebungsname

Beschreibt den Namen des Ordners, in dem die Flyway-Konfigurationen liegen. Die Angabe muss relativ zum Skript erfolgen. Aktuell kann es nur `local` sein.

#### Skriptordner

Ein optionaler Wert. Standard ist `_scripts`. Damit wird der Ordner beschrieben, in dem die Flyway-Skripte liegen, die je Service ausgeführt werden.

## Vorgehen im Fehlerfall

Falls das Skript für einen Service fehlschlägt, wird im `flyway_database_init_history`-File in der Datenbank des Services ein  
Eintrag mit `success=0` angelegt. Durch die `validateOnMigrate`-Konfiguration wird im Anschluss jeder weitere  
Versuch, das Skript neu auszuführen, ebenfalls fehlschlagen. Daher muss der entsprechende Eintrag im  
`flyway_database_init_history`-File manuell entfernt werden.