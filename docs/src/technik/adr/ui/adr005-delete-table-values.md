# Löschen von Werten in Tabellen

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Es soll vermieden werden, dass der Nutzer bei der Erfassung von Daten innerhalb einer Tabelle bereits (teil-)befüllte
Zeilen entfernt, ohne es zu bemerken.

## Entscheidung

Es gibt zwei Arten von Erfassungstabellen mit variablen Zeilen in der Anwendung. Es wird unter den folgenden beiden
Use-Cases unterschieden.

### 1. Löschen einer einzelnen Zeile

- **Aktion**: Mittels Icon-Button (z.B. bei Ereignissen)
- **Prozess**:
  - Der Nutzer erhält einen Hinweisdialog mit konkretem Kontext zur zu löschenden Zeile.
  - Das Löschen muss bestätigt oder kann abgebrochen werden.
- **Entscheidungsgrundlage**:
  - Das WLS ist das führende System.
  - Die Daten werden zuerst im WLS erfasst und dann auf Papier übertragen.

### 2. Löschen von mehreren Zeilen gleichzeitig

- **Aktion**: Mittels Input, der die gesamte Zeilenanzahl widerspiegelt (z.B. bei Stimmabgabevermerken)
- **Prozess**:
  - Der Nutzer erhält einen Hinweisdialog mit dem Kontext, dass Zeilen x-y aufgrund bereits erfasster Daten nicht
  gelöscht werden können.
  - Das Hinweisfenster kann geschlossen werden, aber das Löschen wird verhindert.
- **Entscheidungsgrundlage**:
  - Nicht das WLS, sondern das Papier ist das führende System.
  - Die Daten werden auf Papier erfasst, und das WLS ist eine Abbildung davon.
  - Bei Unstimmigkeiten im WLS reicht eine Korrektur in der Anwendung nicht aus; ein erneuter Abgleich mit den
  Papierdaten ist notwendig.

## Konsequenzen

- Jede Komponente zur Bearbeitung von Daten als Tabelle aktiviert einen Hinweisdialog, sobald Zeilen mit bereits
(teil-)erfassten Daten aus der Tabelle entfernt werden sollen.
- Der Dialog enthält die Information, welche Zeilen das Löschen verhindern.
- Das Löschen mehrerer (teil-)befüllter Zeilen in einem Zuge ist nicht möglich.
- Sind alle zu löschenden Zeilen leer, öffnet sich kein Dialog und sie werden ohne Bestätigung gelöscht.
