# Offlinefähigkeit-Konzept

In den Wahllokalen kann die Internetverbindung instabil sein, was jedoch die Bedienbarkeit des Clients nicht beeinträchtigen darf.
Eine Voraussetzung für die Nutzung ist, dass zu Beginn des Wahltages beim Anmelden eine Internetverbindung verfügbar ist.
Anschließend sollte der Benutzer bis einschließlich des Drucks der Niederschrift durchgehend arbeiten können. Sollte am Ende des Tages
weiterhin keine Verbindung bestehen, wird die Niederschrift im Wahllokal gedruckt und telefonisch übermittelt.
Die Datenübertragung an das Backend kann auch am nächsten Tag dürch das Hochfahren des Notebooks erfolgen.

## I. Beschreibung Offlinefähigkeit

Grundsätzlich läuft die Kommunikation des Clients mit dem Backend über REST Schnittstellen.
Die dahinter verborgenen Ressourcen sind über Schreib- und Lese-Operationen zugänglich.
Sollte der Client offline sein, kann auf die Ressourcen nicht zugegriffen werden.
Offlinefähigkeit beschreibt das Konzept, die Ressourcen lokal im Client abzubilden und mit dem
Backend zu synchronisieren.
Diese Anforderung lässt sich mit einer neuartigen Technologie namens Service Worker (im
folgenden SW) umsetzen.

## II. Anforderungen an die REST-Schnittstellen der Microservices

Um einen möglichst konfigurations- und wartungsarmen Code zu ermöglichen ist es notwendig,
dass alle WLS-Schnittstellen einer Objektart, die Lese- und Schreiboperationen bieten, die
gleiche URL anbieten und die Unterscheidung der Operation einzig und allein anhand der
HTTP-Methode durchgeführt wird. So haben wir zum Beispiel für die Objektart `Eroeffnungsuhrzeit` eine GET und eine POST Operation an die URL: "_/businessActions/eroeffnungsuhrzeit/wahlbezirkID_".

## III. Umgesetztes Verhalten

Beim Lesen und Schreiben werden die Netzwerk-Anfragen des Browsers vom Service Worker
(der als eine Art Middleware aggiert) abgefangen und wahlweise lokal gespeichert oder aus
dem lokalen Speicher geladen.
Die Identifizierung der Anfragen erfolgt dabei allein anhand der URL. Wenn also der Client (mit
Wahlbezirk-ID „123“) beim ersten anmelden (um dem obigen Beispiel zu folgen) die
Eröffnungsuhrzeit lädt, um ggf. bereits erfasste Daten zu laden, wird im lokalen Speicher mit
dem Key "_/businessActions/eroeffnungsuhrzeit/123_" der Wert gespeichert der geladen wird (in unserem Beispiel wäre das JSON-Objekt `Eroeffnungsuhrzeit`).
Arbeitet der Client nun weiter und erfasst eine neue `Eroeffnungsuhrzeit`, wird unter dem obigen
Key der neue Wert gespeichert.

### A. Strategien

Es gibt drei unterschiedliche Strategien, mit denen der SW umgehen kann: `OFFLINE_FIRST`,
`ONLINE_FIRST`, `ONLINE_ONLY`. Im Folgenden sind die Strategien erklärt.

- `OFFLINE_FIRST` (default): Hat der Service Worker Daten im lokalen Speicher, werden diese Daten zurückgeliefert. Nur wenn keine Daten vorhanden sind, wird ein Request ans Backend geschickt;
- `ONLINE_FIRST` Bei Daten wie z.B. den Wahlvorständen, A-Werten und Konfigurationsdaten kann es sein, dass im Backend neuere Daten vorhanden sind. Daher wird mit dieser Strategie zuerst ein Request ans Backend geschickt. Ist dieser erfolgreich, werden die neuen Daten lokal gespeichert und zurückgegeben;
- `ONLINE_ONLY` Diese Strategie wird für schreibende Operationen benötigt. Diese Strategie verhindert, dass der Service Worker die Daten
als `dirty: true` markiert, wenn ein Request fehlschlägt.

### B. Initialisierung

Bei Login am Wahllokalsystem prüft der Client zunächst, welcher Benutzer als letztes an diesem Browser angemeldet war. Ist der aktuelle Benutzer ungleich dem letzten Benutzer, wird die lokale Datenbank gelöscht. Handelt es sich aber um den gleichen Benutzer, bleiben seine Offline erfassten Daten bestehen und er kann weiter arbeiten.
Anschließend wird die Initialisierungsseite des WLS aufgerufen. Auf dieser werden alle lesenden Endpunkte, welche für die aktuelle Systemsituation (Art des Wahllokals, Anzahl und Arten der stattfindenden Wahlen) relevant sind einmalig aufgerufen. Da der SW alle Anfragen unterbricht und speichert, wird mit dieser Aktion sichergestellt, dass alle Daten ab sofort Offline zur Verfügung stehen.

### C. Behandlung der aus- oder eingehenden Requests oder Responses

#### a). Ist `online` und `kein Fehler` tritt auf

In diesem Fall wird davon ausgegangen, dass keine Probleme auftreten.

Der Wahlvorstand speichert seine Daten und diese werden immer erfolgreich im Backend gespeichert.
Alles was der SW in diesem Fall tut ist, seine lokalen Daten aktuell zu halten. Bedeutet: Der Wahlvorstand sendet Daten, diese leitet der SW ans Backend.
Anschließend speichert er die gesendeten Daten wie unter [Umgesetztes Verhalten](#iii-umgesetztes-verhalten) beschrieben.

#### b). Ist `offline` oder `ein Fehler` ist aufgetreten

In diesem Fall wird davon ausgegangen, dass der Wahlvorstand Offline ist, oder Fehler im Backend auftreten.

In jedem der Fälle wird der SW merken, dass Anfragen ans Backend nicht mit einem gültigen HTTP-Statuscode (200, 201 und 204) beantwortet werden.

Passiert dies bei lesenden Anfragen wird, wie unter [Strategien](#a-strategien) beschrieben, mit den Offline vorhandenen Daten geantwortet.

Passiert dies bei schreibenden Anfragen, verschattet der SW den Fehler, sodass der Wahlvorstand nichts von den Problemen sieht. Außerdem werden die soeben gesendeten Daten lokal nicht nur gespeichert sondern auch als `dirty` markiert, sowie der Zeitpunkt, zu dem die Daten zu senden versucht wurden, gespeichert.

### D. Datensynchronisation

Aus [C. b)](#b-ist-offline-oder-ein-fehler-ist-aufgetreten) entsteht die berechtigte Frage:
_Wie kommen die offline-gespeicherten Daten wieder ins Backend_?

Hierfür soll eine Synchronisationskomponente (`Offline-Syncer`) implementiert werden.
Diese Komponente wird bei dem Eintritt von drei Ereignissen aktiv (getriggert):

- beim Wechseln vom `offline` in den `online` Status, die sog. Hintergrund-Synchronisation);
- beim Senden der Ergebnismeldung (Schnellmeldung oder Niederschrift) die sog. Vordergrund-Synchronisation);
- beim Ausloggen des Benutzers.

#### a). Hintergrundsynchronisation beim offline-online Wechsel

Wenn der Wahllokalclient den Zustand von _Offline_ zu _Online_ wechselt, wird der `Offline-Syncer` aktiv.
Dies geschieht im Hintegrund und ist für den Benutzer nur durch eine Einblendung unten rechts auf der Seite erkennbar.

![Sinchronisation im Hintergrund:](/offlinesyncer/SyncInBackground.PNG?url)
[Abbildung 1: Synchronisation im Hintergrund](/offlinesyncer/SyncInBackground.PNG)

Der Syncer prüft, ob in den lokalen Daten mit `dirty=true` markierte Daten vorhanden sind und sortiert diese anhand der ursprünglichen Speicherung-Reihenfolge (`timestamp`).
Dann versucht er jede dieser Datensätze (aus der `indexedDB`) erneut ans Backend zu senden. Bei erfolgreich durchgeführten Anfragen wird das `dirty` auf `false` gesetzt.
Nicht erfolgreiche Anfragen haben keine Konsequenzen. Nachdem alle Anfragen zu synchronisieren versucht wurden, verschwindet die Anzeige unten rechts wieder.

#### b). Vordergrundsynchronisation beim Senden der Ergebnismeldung

Vor dem senden einer Schnellmeldung oder Niederschrift wird der Offline-Syncer im Vordergrund gestartet. Dies ist für den Wahlvorstand durch das Popup “Offlinedaten werden Synchronisiert” ersichtlich.

![Synchronisation im Vordergrund](/public/offlinesyncer/SyncInForeground.PNG?url)
[Abbildung 2: Synchronisation im Vordergrund](/public/offlinesyncer/SyncInForeground.PNG)

Ist die Synchronisation erfolgreich, oder waren keine dirty-Daten vorhanden, wird das Fenster noch eine Sekunde angezeigt, damit der Wahlvorstand die Möglichkeit hat das Ergebnis zu sehen. Anschließend wird die Schnellmeldung bzw. Niederschrift gesendet.

Ist das Synchronisieren nicht erfolgreich, weil bestimmte Daten aufgrund von schlechter Netzverbindung oder nicht erreichbaren Services nicht gesendet werden konnten, wird KEINE Schnellmeldung/Niederschrift gesendet, weil das Wahllokalsystem nicht sicherstellen kann, dass alle Ergebnisrelevanten Daten übermittelt werden konnten. In diesem Falle kann der Wahlvorstand als nächsten Schritt seine Schnellmeldung/Niederschrift ausdrucken, auf der, durch den Offline-Druck, garantiert alle Relevanten, aktuellen und korrekten Daten stehen werden.

#### c). Vordergrundsynchronisation beim Ausloggen des Benutzers

Wie oben [unter b beim Senden der Ergebnismeldung](#b-vordergrundsynchronisation-beim-senden-der-ergebnismeldung) wird auch vor dem Ausloggen eines Benutzers versucht,
falls `dirty=true`-markierte Daten in der `IndexedDB` vorhanden, diese ans Backend zu senden.

### E. Logout eines Benutzers

Daten werden NICHT gelöscht, wenn ein Nutzer sich abmeldet. Dadurch wird verhindert, dass durch

1. Koffertausch
2. Abmeldung durch Inaktivität
3. Schließen des Tabs
4. Etc.

Offline erfasste Daten verloren gehen. Die Daten bleiben solange im Offline-Speicher enthalten, bis sich ein
anderer Benutzer am gleichen Rechner anmeldet (siehe [Initialisierung](#b-initialisierung)).
