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
Sie teilen sich auf in die Anzahl an Wahlberechtigten, die keinen Wahlschein erhalten haben (A1)
und in die Anzahl an Wahlberechtigten, die einen Wahlschein erhalten haben und deshalb einen W-Vermerk im 
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
- Bei Nichterfolg wird geprüft, ob im lokalen Repository wenigstens 'alte' A-Werte existieren.
- Falls weder im externen Wahlsystem noch im lokalen Repository A-Werte gefunden werden
  liefert der Service einen Fehler. 

### Update des Status der Ergebnisermittlung

Bei der Auszählung (Ergebnisermittlung) wird zuerst eine Schnellmeldung vorbereitet. Den Abschluss der Auszählung
bildet die Niederschrift. Die Schnellmeldung und die Niederschrift können gedruckt und übermittelt werden.

Welche Aktionen für die jeweiligen Dokumente bereits in einem Wahllokal vollzogen wurden, wird über den Status gepflegt.

Über Änderungen an dem Status wird auch der [Monitoring-Service](/services/monitoring-service/) informiert.

### Lesen und Schreiben von Stimmabgabevermerken

####  Erfassung der Stimmabgaben mit Wahlschein
Im Wählerverzeichnis wird bei einer Wahl durch den Schriftführer über den sogenannten "Stimmabgabevermerk" vermerkt,
wenn ein Wahlberechtigter mit Wahlschein seinen Stimmzettel in die Urne im Wahllokal gelegt hat.
Über das Schreiben und Lesen der Wahlscheine kann die aktuelle Anzahl an Stimmabgabevermerken an WLS übermittelt
bzw. ausgelesen werden.

### Erfassung der Anzahl an Stimmzettelumschlaegen
Bei der Briefwahl zählt der Wahlvorstand die Stimmzettelumschlaege.
Über das Schreiben und Lesen der Stimmzettelumschlaege kann die aktuelle Anzahl eines Wahlbezirkes für eine Wahl an WLS 
übermittelt bzw. ausgelesen werden.

### Lesen und Schreiben von Ausdrucken
Hier können Niederschrift(V1) und Schnellmeldung(V3) gespeichert und gelesen werden.

####  Speichern eines Ausdrucks mit bestimmter Meldungsart für einen Wahlbezirk einer Wahl
Speichert einen Ausdruck der entsprechenden Meldungsart für einen Wahlbezirk einer Wahl.
Es wird automatisch ein aktuelles Erstellungsdatum mit erzeugt und gespeichert.

### Lesen eines Ausdrucks mit bestimmter Meldungsart für einen Wahlbezirk einer Wahl
Liest einen Ausdruck mit entsprechender Meldungsart für einen Wahlbezirk einer Wahl.

### Lesen aller Ausdrucke für einen Wahlbezirk einer Wahl
Liest alle Ausdrucke (V1 und V3) für einen Wahlbezirk einer Wahl.

### Lesen und Schreiben von Ergebnissen
Wahlergebnisse können gespeichert werden. Die Ergebnisse werden über ihren Wahlbezirk, ihre Wahl und den Stapel,
auf dem sie liegen, ausgelesen.
Es können auch die Ergebnisse von allen Stapeln einer Wahl in einem Wahlbezirk gelesen werden.