# Tips und Tricks

## Zugriff auf Datenbanktabellen aller Services in einer Verbindung

Für jeden Service gibt es einen separaten User für die Oracle Datenbank die in Docker läuft. Diese User können
nur die Tabellen sehen, die im jeweiligen Schema sind. Der User `wls_basisdaten_service` kann
z.B. nur die Tabellen `handbuch`, `kandidat` oder `wahlvorschlag` sehen.

<details>

<summary>Screenshots</summary>

![mehrere Schemata ausgewählt](/tipsAndTricks/MultipleSchemasSelected.png)  
*Auch wenn mehrere Schemata ausgewählt sind ...*

![nur Basisdatenservicetabellen sichtbar](/tipsAndTricks/OnlyBasisdatenServiceTablesAccessible.png)  
*so sieht man doch nur die Tabellen zum Basisdatenservice*

</details>

Mit dem User `system` kann man auf alle Schemata zugreifen und hat und somit Zugriff auf alle Tabellen für alle
Services. Das Passwort ist der Wert von `ORACLE_PASSWORD` aus dem `docker-compose.yml`-File. Dort sind auch die
Verbindungsdaten enthalten die man zum Einrichten des Zugriffs benötigt.

<details>

<summary>Screenshot</summary>

![Tabellen zu allen Services sind verfügbar](/tipsAndTricks/MutlipleSchemasAccessible.png)  
*so sieht man doch nur die Tabellen zum Basisdatenservice*

</details>