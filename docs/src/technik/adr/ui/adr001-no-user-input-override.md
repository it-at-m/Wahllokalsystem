# Keine Änderungen von Werten durch die Anwendung

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Beim `VNumberInput` ist es möglich, einen `min`- oder `max`-Wert zu definieren. Geben die Nutzer\*innen einen Wert
außerhalb dieses Bereichs an, wird der Wert automatisch in den gültigen Bereich gebracht.

Bei Textfeldern ist es möglich, eine maximale Länge zu definieren. Ist diese Länge erreicht und die Nutzer\*innen geben 
weiteren Text ein, wird dieser Text ignoriert.

## Entscheidung

Die Anwendung darf keine Werte anpassen, ohne dass die Nutzer\*innen darüber informiert werden.

## Konsequenzen

Der Wertebereich, sei es ein Maximum oder Minimum oder eine begrenzte Länge, muss durch `rules` abgesichert werden.
