# Getting started

## Formatter einrichten

Im Projekt verwenden wir `checkstyle` und `spotless` um für einen möglichst einheitlichen Codestyle zu sorgen.
Dazu haben wir Regeln definiert. Diese Regeln und deren Hinterlegung in der jeweiligen IDE ist
[hier](https://github.com/it-at-m/itm-java-codeformat) beschrieben.

## Zusammenspiel IDE mit Podman

> [!NOTE] Konfiguration von hosts
>
> Im hosts-File müssen folgende Enträge enthalten sein, damit das Wahllokalsystem lokal korrekt funktioniert
>
> - 127.0.0.1 host.docker.internal
> - 127.0.0.1 auth.wls.host.docker.internal
> - 127.0.0.1 gui.wls.host.docker.internal

![Aufteilung Komponenten auf dem Entwicklungs-PC](/developmentPCEnvironment.png)

Auf dem Entwicklungs-PC müssen Container ausführbar sein. Einige Komponenten, die zur Ausführung des Systems
notwendig sind, liegen nur als Image vor. Die Komponenten sind mit `podman only` markiert. Alle
anderen Komponenten liegen sowohl als Image als auch anderweitig startbar vor. Die Microservices können über Skripte
oder `Run Configurations` (IntelliJ) ausgeführt werden.

> [!NOTE]
>
> Damit Services, die in Podman laufen, mit Services außerhalb von Podman kommunizieren können, müssen die Container
> das entsprechende Host-Gateway konfiguriert haben.

> [!CAUTION] Zugriff bei Frontendentwicklung
>
> Bei der Entwicklung am Frontend sollte der Zugriff über `host.docker.internal:8083` erfolgen, da nur dadurch das
> Hot Module Replacement (HMR) des Vite-Servers funktioniert. Bei Zugriff über `gui.wls.host.docker.internal:58083`
> kann die WebSocket-Verbindung nicht aufgebaut werden, und HMR funktioniert nicht.

## Services und Ports

Übersicht über die Services und auf welchen Port sie im Standard lauschen:

> [!IMPORTANT]
> Diese Ports werden sowohl in der IDE als auch in Podman verwendet.
> Beachten Sie, dass somit nur eine Instanz eines Services gleichzeitig laufen kann.

| Service                                                                                   | Port |
|-------------------------------------------------------------------------------------------|------|
| [Admin](/services/backend-services/admin-service/)                                        | 8209 |
| [Auth](/services/backend-services/auth-service/)                                          | 8100 |
| [Basisdaten](/services/backend-services/basisdaten-service/)                              | 8205 |
| [Briefwahl](/services/backend-services/briefwahl-service/)                                | 8202 |
| [Broadcast](/services/backend-services/broadcast-service/)                                | 8200 |
| [EAI](/services/backend-services/eai-service/)                                            | 8300 |
| [Ergebnismeldung](/services/backend-services/ergebnismeldung-service/)                    | 8208 |
| [Infomanagement](/services/backend-services/infomanagement-service/)                      | 8201 |
| [Monitoring](/services/backend-services/monitoring-service/)                              | 8206 |
| [Vorfälle und Vorkommnisse](/services/backend-services/vorfaelleundvorkommnisse-service/) | 8204 |
| [Wahlvorbereitung](/services/backend-services/wahlvorbereitungs-service/)                 | 8203 |
| [Wahlvorstand](/services/backend-services/wahlvorstand-service/)                          | 8207 |

## Profile

| Profilname             | Beschreibung                                                                                                                             |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| db-h2                  | Als Datenbank wird eine embedded H2 im Service verwendet.                                                                                |
| db-oracle              | Als Datenbank wird eine Oracle Datenbank verwendet. Im Standard wird die DB-Datenbank aus dem Stack (Podman) verwendet.                  |
| db-dummydata           | Es werden Flyway-Files mit Dummydaten für die Datenbank mit verwendet.                                                                   |
| no-security            | Die Prüfungen der Authentifizierung und Authorisierung werden deaktiviert.                                                               |
| dummy.nobezirkid.check | Deaktiviert die Prüfung, dass Anfragen für einen bestimmten Wahlbezirk (wahlbezirkID), nur von dem User des Wahlbezirkes erfolgen dürfen |
| dummy.clients          | Es erfolgt keine Kommunikation mit fachlichen Services weil Dummy-Implementierung anstatt von Clients verwendet werden.                  |
| standalone             | Der Service arbeitet eigenständig. Inkludiert dummy.clients, dummy.nobezirkid.check und db-h2.                                           |
| local                  | Der Service läuft lokal. Entsprechend wird der Port des Services definiert um nicht in Konflikt mit anderen Service zu kommen.           |
| plainTextLogging       | Logmeldungen werden als Text ausgegeben. Ohne dieses Profil sind die Logmeldungen ein JSON-Objekt.                                       |

## Benutzer

| Name         | Passwort | Beschreibung                                                                                                           |
|--------------|----------|------------------------------------------------------------------------------------------------------------------------|
| wls_all_bwb  | test     | Ein Benutzer mit der Rolle Monitoring_Helpdesk für das Admintool                                                       |
| wls_all_uwb  | test     | Ein Benutzer mit der Rolle Monitoring_Helpdesk für das Admintool                                                       |
| wls_komw_bwb | test     | Ein Benutzer mit der Rolle Wahlvorstand für eine Kommunalwahl mit OBW und SRW sowie der WahlbezirksArt BWB (Briefwahl) |
| wls_komw_uwb | test     | Ein Benutzer mit der Rolle Wahlvorstand für eine Kommunalwahl mit OBW und SRW sowie der WahlbezirksArt UWB (Urnenwahl) |
| wls_mbw_bwb  | test     | Ein Benutzer mit der Rolle Wahlvorstand für eine Migrationsbeiratswahl und der WahlbezirksArt BWB (Briefwahl)          |
| wls_mbw_uwb  | test     | Ein Benutzer mit der Rolle Wahlvorstand für eine Migrationsbeiratswahl und der WahlbezirksArt UWB (Urnenwahl)          |

> [!CAUTION]
> Für die Anmeldung am WLS muss der User die Rolle `WLS_WAHLVORSTAND` haben.
> Für die Anmeldung am Admintool muss der User die Rolle `MONITORING_HELPDESK` haben.

## Datenbank

Der Zugriff auf die Oracle-Datenbank über die IDE ist gemäß [dieser Anleitung](/technik/guides/db-access) einzurichten.

## Runconfigurations

Für die Spring-Boot Microservices werden für IntelliJ Runconfigurations zur Verfügung gestellt. Diese sind
gruppiert nach der Art der Datenbank und ob die Security aktiviert ist oder nicht.

Eine Übersicht über die Profile gibt es [hier](#profile).

> [!TIP]
> Wenn es Updates an den Runconfigurations gab, die gefetcht wurden, ist es notwendig IntelliJ neu zu starten.

## Podman

Für die Nutzung von Podman ist im `docker-compose.yml` File `host.docker.internal` als DNS-Name konfiguriert,
damit die Kommunikation zwischen Containern und dem Host-Computer vereinfacht wird. Dabei ist darauf zu achten,
dass im `hosts` File unter `C:\Windows\System32\drivers\etc` der folgende Eintrag existiert,
sodass `host.docker.internal` auf `localhost` verweist:

```text
127.0.0.1 host.docker.internal
```

## Starten des Frontends

Standardmäßig wird das Frontend über den Befehl `"dev": "vite"` in der `package.json`-Datei gestartet.

Nachdem das Frontend in der IDE und das ApiGateway über Podman gestartet wurde, kann es über `http://localhost:8400/`
aufgerufen werden. Allerdings befindet sich die Oberfläche dann in einer Ladeschleife und man sieht nur einen
flackernden Bildschirm. Um diese Schleife während der Entwicklung zu umgehen, gibt es zwei Möglichkeiten:

### 1. Starten über das Gateway + Authentifizierung {#start-via-gateway}

Eine Möglichkeit, die Ladeschleife zu umgehen, ist es, sich lokal mit einem der [User](#benutzer) anzumelden.
Nachdem das Frontend über die IDE gestartet wurde, muss die URL `http://localhost:8083/` mit dem Port `8083` aufgerufen
werden, um auf die Login-Seite zu kommen. Nach der Anmeldung bleibt man auf dem Port `8083`, wird aber vom Gateway
zum Frontend weitergeleitet und die Ladeschleife ist weg.

> [!NOTE]
> Der Anmeldevorgang muss jedes Mal wiederholt werden, sobald das ApiGateway neu gestartet wird.

### 2. `no-security`-Profil

Die zweite Möglichkeit ist es, das ApiGateway mit dem `no-security`-Profil zu starten.
Dazu muss im `/stack/docker-compose.yml` File beim Service refarch-gateway unter environment in der Zeile
`- SPRING_PROFILES_ACTIVE=hazelcast-local` das Profil `no-security` hinzugefügt werden. Damit die Änderung wirksam wird,
sollte der Container in Podman einmal komplett gelöscht und über das `docker-compose.yml` File neu gestartet werden.

> [!WARNING]
> Bei dieser Variante ist es wichtig, dass die Änderung im `docker-compose.yml` File nicht gepusht wird, weil alle
> anderen Container mit security laufen und das `no-security`-Profil nur für die Entwicklung benötigt wird.

### Backend-Services

Damit das Frontend im Zusammenspiel mit den anderen Services lokal gestartet werden kann, sind einmalig nach dem Aufsetzen der DB folgende Schritte notwendig:

1. `db-oracle`-Service in Podman oder über das `docker-compose.yml` starten
2. `auth-service` starten
3. `refarch-gateway-wls`-Service in Podman oder über das `docker-compose.yml` starten
4. Das Wahllokalsystem-Frontend in Podman oder über `npm run dev` starten
   (siehe [Punkt 1](#start-via-gateway): *Jetzt kann das Frontend zwar aufgerufen werden, aber durch das fehlgeschlagene Laden der initialen Daten ist noch kein Zugriff auf die Anwendung möglich)*
5. die Services `basisdaten-service`, `eai-service`, `ergebnismeldung-service`, `infomanagement-service` und `wahlvorstand-service` starten und die
   http-requests aus dem File für die gewünschte Wahl ausführen (Kommunalwahl mit OBW und SRW: [`initOracleDB_KomW.http`](https://github.com/it-at-m/Wahllokalsystem/blob/dev/stack/http_requests/initOracleDB_Komw.http) oder Migrationsbeiratswahl: [`initOracleDB_MBW.http`](https://github.com/it-at-m/Wahllokalsystem/blob/dev/stack/http_requests/initOracleDB_MBW.http))
