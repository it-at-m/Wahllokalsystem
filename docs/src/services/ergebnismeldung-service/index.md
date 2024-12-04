# Ergebnismeldung-Service

Service der Informationen und Operationen für die Ergebnisse einer Wahl bereitstellt. Dabei geht es um die Ergebnisse
die durch den Nutzer anhand von Masken erfasst werden.

## Abhängigkeiten

Folgende Services werden benötigt:
- Basisdaten-Service
- Briefwahl-Service
- EAI-Service
- Infomanagement-Service
- Monitoring-Service
- Wahlvorbereitung-Service

## Daten und Funktionen

- Verwalten und Übermitteln von Ergebnismeldungen
- Verwalten von A- und B-Werten

### Abrufen von A-Werten (Wahlberechtigte)
Die Methode `getAWerte()` liefert die A-Werte für eine bestimmte Wahlbezirk-ID.
Die A-Werte repräsentieren die Anzahl der Wahl- bzw. Stimmberechtigten in einem Wahlbezirk.
Sie teilen sich auf in die Anzahl an Wahlberechtigten die keinen Wahlschein erhalten haben (A1)
und in die Anzahl an Wahlberechtigten die einen Wahlschein erhalten haben und deshalb einen W-Vermerk im 
Wählerverzeichnis besitzen (A2).


- Wenn über die EAI im externen Wahlsystem Daten gefunden werden, dann werden diese im lokalen Repository gespeichert
und zurück gegeben.
- Sofern über die EAI im externen Wahlsystem keine Daten gefunden werden können, wird versucht auf 'alte' A-Werte im 
lokalen Repository zuzugreifen.
- Falls weder im externen Wahlsystem noch im lokalen Repository A-Werte gefunden werden
liefert der Service einen Fehler. 

### Initialisieren von A-Werten (Wahlberechtigte)
Die Methode initialisiereAWerte() initialisiert die Wahlberechtigten (A-Werte) für eine Liste an Wahlbezirk-IDs.

- Für jede Wahlbezirk-ID in der Liste wird über die EAI im externen Wahlsystem nach A-Werten gesucht. Bei Erfolg werden
diese im lokalen Repository gespeichert.
- Bei Nichterfolg, wird geprüft ob im lokalen Repository wenigstens 'alte' A-Werte existieren.
- Falls weder im externen Wahlsystem noch im lokalen Repository A-Werte gefunden werden
  liefert der Service einen Fehler. 