# Abweichung von Tabreihenfolge in Leserichtung, wenn notwendig

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Es gibt Masken, bei denen Aufgrund des Prozesses Eingabe von Daten nicht der Leserichtung entspricht.

## Entscheidung

Sofern nicht anders definiert, ist die Tabreihenfolge entsprechend der Leserichtung. Ausnahmen davon sind explizit zu
definieren.

## Konsequenzen

### Abweichung: Auszählung gültiger Stimmen bei SRW, BAW und MGW

Bei der Erfassung der gültigen Stimmen je Wahlvorschlag, **nicht** der Kandidaten, erfolgt die Erfassung
Spaltenweise. Zuerst werden die Stimmen je Wahlvorschlag von Stapel A erfasst. Danach werden die erfassten
Stimmen von Stapel B für alle Wahlvorschläge erfasst.