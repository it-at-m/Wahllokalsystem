# Begrenzte Interaktionsmöglichkeiten bei Benutzereingaben

## Status

<adr-status status='accepted'></adr-status>

## Kontext

`VNumberInput` bietet die Möglichkeit, die Zahl zusätzlich über Spinner-Buttons oder die Pfeiltasten `↑` und `↓`
zu verändern.

## Entscheidung

Die Eingabemöglichkeiten sollen sich auf die direkte Eingabe beschränken.

## Konsequenzen

### VNumberInput

Es soll nur möglich sein, Werte über die Zahlentasten der Tastatur direkt zu erfassen.

Die Interaktionsmöglichkeiten bei `VNumberInput` wurden beschränkt. Spinner-Buttons sind bei der
Eingabe nicht mehr verfügbar, und eine Veränderung des Wertes über die Pfeiltasten ist ebenfalls nicht mehr möglich.
