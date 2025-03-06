# Wahlvorstand-Service

Service für Themen im Zusammenhang mit Wahlvorständen und Anwesenheiten.

Jedes WahlvorstandsMitglied hat eine Funktion, die von der entsprechenden WahlbezirksArt und Wahlart abhängig ist.
Der konkrete Funktionsname wird aktuell vom Service gemappt und bereitgestellt.

**Beispiel:**

- `UWB.BTW.W` = Wahlvorsteher\*in (Funktion W) der Bundestagswahl (Wahlart BTW) in einem Urnenwahlbezirk (Wahlbezirksart UWB)
- `BWB.BEB.SWB` = Stellvertretung Briefwahlvorsteher\*in (Funktion SWB) des Bürgerentscheids (Wahlart BEB) in einem Briefwahlbezirk (Wahlbezirksart BWB)

## Abhängigkeiten

Folgende Services werden für den Betrieb benötigt:

- Basisdaten-Service
- EAI-Service
- Infomanagement-Service

## Daten und Funktionen

- Abrufen der Wahlvorstände der Wahlbezirke
- Pflege der Anwesenheiten der Wahlvorstände

### Wahlart

Es gibt die folgenden Wahlarten

### Wahlvorstand abrufen

Die `getWahlvorstand()`-Methode stellt einen Mechanismus zum Abrufen von Wahlvorstandsinformationen bereit, der sowohl lokale- als auch remote-Datenabrufe unterstützt.

- Ist das optionale Flag `forceUpdate` auf true gesetzt, werden die aktuellsten remote-Daten abgerufen und die lokalen Daten überschrieben.

- Durch die Implementierung eines Fallback-Mechanismus wird sichergestellt, dass der Client auch im Falle einer fehlenden Antwort eine valide Rückmeldung erhält.
