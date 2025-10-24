# Anordnung des active Button

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Bei der Verwendung von mehreren Buttons wurden diese bisher in aufsteigender Wertigkeit angeordnet,
sodass der aktive Button für die primäre Aktion immer rechts war. Laut Material Design soll jedoch in der
[Card-Komponente](https://m3.material.io/components/cards/accessibility#7b78d688-0bcc-4013-93fa-373ea912841d)
der Primärbutton den ersten Platz einnehmen.

## Entscheidung

Um der Empfehlung von Material Design zu folgen, wird der Primärbutton innerhalb der Aktionen einer Card zuerst
dargestellt. Dies ist in den meisten Fällen der Button zum Speichern bzw. Fortsetzen des Wahlablaufs.

## Konsequenzen

Für die Benutzer ist innerhalb einer Card der Button mit der höchsten Priorität als erstes dargestellt.
Anders verhält sich dies für die Buttons in einem Dialog. Hier sind die Buttons so angeordnet, dass die ablehnende
Aktion links vom Button für die Bestätigung platziert ist.
([siehe. Material Design](https://m3.material.io/components/dialogs/guidelines#befd7f4d-1029-4957-b1b5-da13fc0bbf3c))
