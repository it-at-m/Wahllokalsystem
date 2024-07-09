# EAI-Service

Das Wahllokalsystem hängt von anderen Anwendungen ab, an die es Daten sendet und von den es Daten importiert.
Die Schnittstelle zwischen diesen Anwendungen wird über diesen EAI-Service realisiert.

## Abhängigkeiten

Der Service hat keine Abhängigkeiten zu anderen Services.

## Daten und Funktionen

### Wahldaten

Es können Informationen zu den Wahltagen, Wahlbezirken, Wahlen, Wahlbasisdaten und Wahlberechtigten abgerufen werden.

### Wahlvorschläge

Es können die Wahlvorschläge und Referendumsvorschläge geladen werden.

### Wahlvorstandsdaten

Es können die Wahlvorstände für die Wahlbezirke geladen werden sowie deren Anwesenheit mitgeteilt werden.

### Wahlergebnisse

Das Ergebnis das in den Wahllokalen ermittelt wurde kann mitgeteilt werden.

### Wahlbeteiligung

Wahlbeteiligungen der Wahllokale kann mitgeteilt werden.

### Wahllokalstatus

Der Zustand in dem sich die Wahllokale befinden kann mitgeteilt werden.

## Zugriffsbeschränkungen

Übersicht über die Endpunkte und die dafür benötigten Rechte:

| Endpunkt | erforderliche Rechte |
| --- | --- |
| GET /wahldaten/wahlbezirke/{wahlbezirkID}/wahlberechtigte | aoueai_BUSINESSACTION_LoadWahlberechtigte
| GET /wahldaten/wahltage?includingSince | aoueai_BUSINESSACTION_LoadWahltage
| GET /wahldaten/wahlbezirk?forDate&withNummer | aoueai_BUSINESSACTION_LoadWahlbezirke
| GET /wahldaten/wahlen?forDate&withNummer | aoueai_BUSINESSACTION_LoadWahlen
| GET /wahldaten/basisdaten?forDate&withNummer | aoueai_BUSINESSACTION_LoadBasisdaten