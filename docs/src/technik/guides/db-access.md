# Zugriff auf Datenbanktabellen aller Services in einer Verbindung

Für jeden Service gibt es ein separates Benutzerkonto für die Oracle Datenbank, die in Podman läuft.
Diese Benutzerkonten können nur die Tabellen sehen, die im jeweiligen Schema sind. Mit dem Benutzerkonto
`wls_basisdaten_service` kann z.B. nur auf die Tabellen `handbuch`, `kandidat` oder `wahlvorschlag` zugegriffen werden.

<details>

<summary>Screenshots</summary>

![mehrere Schemata ausgewählt](/tipsAndTricks/MultipleSchemasSelected.png)  
_Auch wenn mehrere Schemata ausgewählt sind ..._

> [!IMPORTANT]
> Sollten die Schemas der Services nicht sichtbar sein, empfiehlt es sich die Richtigkeit der DB-URL unter den Properties
> der Datasource zu prüfen. Die URL soll `jdbc:oracle:thin:@//localhost:1521/XEPDB1` sein.

![nur Basisdatenservicetabellen sichtbar](/tipsAndTricks/OnlyBasisdatenServiceTablesAccessible.png)  
_so sieht man doch nur die Tabellen zum Basisdatenservice_

</details>

Mit dem Benutzerkonto `system` kann man auf alle Schemata zugreifen und hat somit Zugriff auf alle Tabellen aller
Services. Das Passwort ist der Wert von `ORACLE_PASSWORD` aus dem `docker-compose.yml`-File. Dort sind auch die
Verbindungsdaten enthalten die man zum Einrichten des Zugriffs benötigt.  
Mit IntelliJ kann man bei den Properties der entsprechenden Datasource unter dem Reiter 'Schemas' auswählen, welche
Schemata man zusätzlich angezeigt haben möchte. Per Default ist nur das Schema SYSTEM zu sehen.

<details>

<summary>Screenshot</summary>

![Tabellen zu allen Services sind verfügbar](/tipsAndTricks/MutlipleSchemasAccessible.png)  
_Zugriff auf alle Tabellen der verschiedenen Schemata_

</details>
