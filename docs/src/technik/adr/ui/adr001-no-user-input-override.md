# Keine Änderungen von Werten durch die Anwendung

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Beim `VNumberInput` ist es möglich, einen `min`- oder `max`-Wert zu definieren. Gibt der Nutzer einen Wert
außerhalb dieses Bereichs an, wird der Wert automatisch in den gültigen Bereich gebracht.

Bei Textfeldern ist es möglich, eine maximale Länge zu definieren. Ist diese Länge erreicht und der Nutzer gibt weiteren Text ein, wird dieser Text ignoriert.

## Entscheidung

Die Anwendung darf keine Werte anpassen, ohne dass der Nutzer darüber informiert wird.

## Konsequenzen

Der Wertebereich, sei es ein Maximum oder Minimum oder eine begrenzte Länge, muss durch `rules` abgesichert werden.
