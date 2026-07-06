# Erfassten Nutzerdaten bereinigen

## Ausgangslage

In der Datenbank sind sowohl Daten aus dem Import als auch von Daten die von den Benutzer\*Innen erfasst wurden.
Ein ungefiltertes komplettes Löschen ist somit nicht möglich, da sonst die Wahl neu importiert werden muss und 
Konfigurationsparameter in der Datenbank gesetzt werden müssen.

Trotzdem ist es notwendig dass man Daten, die über das WLS erfasst wurden, löschen kann, z. B. um den gesamten Erfassungsprozess
frisch zu beginnen.

## Lösung

Über ein Datenbankskript können alle Daten die durch Nutzer erfasst werden gelöscht werden. Das Skript ist
sehr rudimentär. Es ist weder eine Filterung auf einen Benutzer, noch auf eine Wahl möglich.

Das Skript liegt im `stack` unter `scripts` im Ordner `database-init`. Um die Daten zu das Bash-Skript mit dem Kommando
`./run-migration.sh local` ausführen.

In der Datei `README.md` im Skriptordner lieht eine Beschreibung und Erklärung zu den Parametern des Skripts.