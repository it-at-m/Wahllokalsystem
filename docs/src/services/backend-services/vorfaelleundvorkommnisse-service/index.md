# Vorfälle und Vorkommnisse-Service

Service zum Erfassen und Einsehen von Ereignissen: Vorfälle (während der Stimmabgabe von 8:00 bis 18:00 Uhr) und Vorkommnisse (während der Auszählung)

## Abhängigkeiten

Der Service hat keine Abhängigkeiten zu anderen Services.

## Daten und Funktionen

- Verwalten und Übermitteln von Vorfällen und Vorkommnissen
- Laden und Anzeigen besonderer Ereignisse

## Fachliches Datenmodell

```mermaid
classDiagram
        direction TD

        class Ereignisse {
            wahlbezirkID: String
            keineVorfaelle: Boolean
            keineVorkommnisse: Boolean
        }

        class Ereignis {
            beschreibung: String
            uhrzeit: Timestamp
        }

        class EreignisartEnum {
            <<enum>>
            Vorfall
            Vorkommnis
        }

        Ereignisse "1" --> "0..n" Ereignis : ereigniseintraege
        Ereignis "1" --> "1" EreignisartEnum : ereignisart
```
