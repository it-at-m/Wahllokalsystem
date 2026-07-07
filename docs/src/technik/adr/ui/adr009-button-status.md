# Anzeige der Buttons bei unvollständigen Formularen

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Ein einheitliches Erscheinungsbild der Anwendung erfordert klare visuelle Signale für den Benutzer. Nicht durchführbare Aktionen im Workflow müssen eindeutig erkennbar sein.

## Entscheidung

Buttons bleiben so lange deaktiviert (disabled), bis alle für den Button erforderlichen Pflichtfelder vollständig und korrekt ausgefüllt sind.

## Konsequenzen

* **Validierungszeitpunkt:** Die Überprüfung eines Eingabefeldes erfolgt direkt beim Verlassen des Feldes.
* **Direktes Feedback:** Validierungsfehler werden sofort am Feld angezeigt, um dem Benutzer zu signalisieren, warum der Button blockiert ist.
* **Dynamischer Statuswechsel:** Sobald das letzte Pflichtfeld valide ist, schaltet der Button ohne Verzögerung in den aktiven Zustand.

## Ausnahmen

Im Bereich ***Stimmabgabe*** gilt für die Wahlschein-Prüfung eine Sonderregelung:

* **Verhalten:** Der *Suchen*-Button ist permanent aktiv.
* **Eingabe:** Das Feld für die Wahlscheinnummer ist optional und mehrfach nutzbar.
* **Validierung:** Die Prüfung startet erst nach dem Klick auf die Such-Schaltfläche.
* **Ziel:** Dem Benutzer wird verdeutlicht, dass diese Suche optional ist und nicht den Haupt-Workflow blockiert.
