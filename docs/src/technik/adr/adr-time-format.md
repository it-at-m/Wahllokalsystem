# Uhrzeitformat in Texten der Anwendung

## Status

<adr-status status='accepted'></adr-status>

## Kontext

An verschiedenen Stellen innerhalb der Anwendung werden Uhrzeiten angezeigt.
Je nach Kontext und anzuzeigender Uhrzeit haben die Minuten und Sekunden keine Relevanz, sodass diese weggelassen werden können.

## Entscheidung

Uhrzeiten werden prinzipiell im Format `hh:mm` angezeigt.

Es gibt folgende Ausnahmen:

- volle Stunden werden ohne Minuten und Sekunden angezeigt (`18:00 Uhr` wird zu `18 Uhr`)
- die WLS-Clock und die letzte Absendezeit beim Wahlvorstand wird mit Sekunden angezeigt
- in fremdgesteuerten Drucktemplates (Ergebnismeldung) wird das Uhrzeitformat unverändert übernommen

## Konsequenzen

- Vereinheitlichung des Uhrzeitformats führt zu einem besseren Verständnis der Zeitangaben
- Es werden nur die relevanten Teile der Uhrzeit angezeigt
