# Basisdaten-Service

Service zur Bereitstellung folgender Basisdaten:

- Wahltage
- Wahlen
- Wahlbezirke
- Wahlvorschläge
- Kopfdaten
- Handbuch
- Ungültige Wahlscheine

Wahlen, Wahlbezirke und Kopfdaten können in der Service-Datenbank gespeichert werden.

## Abhängigkeiten

Folgende Services werden zum Betrieb benötigt:
- EAI-Service
- Infomanagement-Service

## Datenmodell

```mermaid

classDiagram
    Wahltermindaten
    Wahlbezirk
    Wahlvorschläge
    Referendumvorlagen
    Kopfdaten
    
    Wahltermindaten --> Wahlbezirk : uses
    Wahltermindaten --> Kopfdaten : uses
    Wahltermindaten --> Wahlvorschläge : uses
    Wahltermindaten --> Referendumvorlagen : uses
    
    class Wahltermindaten {
        <<virtual>>
        LocalDate wahltag
 }
```

## Handbuch

In dem Service werden Handbücher verwaltet. Je Wahl und Wahlbezirkart kann ein Handbuch hinterlegt werden.

Bei dem Handbuch soll es sich um ein PDF-Dokument handeln. 

## Ungültige Wahlscheine

Für Wahltage können Listen mit ungültigen Wahlscheinen verwaltet werden. Die Übermittlung der Daten erfolgt
im csv-Format. Dieses umfasst eine Headerzeile, gefolgt von den Daten in den Spalten Name, Vorname und Nummer.

## Wahltermindaten

Wahltermindaten sind eine virtuelle Zusammenfassung von Daten zu einem Wahltermin.

Über den Service können diese Daten für einen Wahltag erstellt und gelöscht werden. Die zu erstellenden Daten
werden von dem EAI-Service importiert.

### Ablauf des Imports

```mermaid

sequenceDiagram
    
    box BasisdatenService
        participant WahltermindatenService
        participant WahltagService
        participant AsyncWahltermindatenService
    end
    box EAIService
        participant Basisdaten
        participant Wahlvorschlaege
        participant Referendumvorlagen
    end
    
    WahltermindatenService ->>+ WahltagService: request für Wahltag mit ID
    WahltagService -->>- WahltermindatenService: Wahltag
    
    WahltermindatenService ->>+ Basisdaten : request Basisdaten für Wahltag
    Basisdaten -->>- WahltermindatenService : Basisdaten
    
    WahltermindatenService ->> WahltermindatenService : Speichere Wahlen aus Basisdaten    
    WahltermindatenService ->> WahltermindatenService : Speicher Wahlbezirke der Basisdaten
    WahltermindatenService ->> WahltermindatenService : Speicher Kopfdaten der Basisdaten
    
    WahltermindatenService -->> AsyncWahltermindatenService : Starte Initialisierung der<br>Wahlvorschläge und Referendumvorlagen
    
    AsyncWahltermindatenService ->>+ Wahlvorschlaege : lade request Wahlvorschläge für Bezirke der Wahlen
    Wahlvorschlaege -->>- AsyncWahltermindatenService : Wahlvorschläge
    AsyncWahltermindatenService ->> AsyncWahltermindatenService : speichere Wahlvorschläge

    AsyncWahltermindatenService ->>+ Referendumvorlagen : request Referendumvorlagen für Bezirke der Wahlen
    Referendumvorlagen -->>- AsyncWahltermindatenService : Referendumvorlagen
    AsyncWahltermindatenService ->> AsyncWahltermindatenService : speichere Referendumvorlagen
```

### Fortschritt

Der aktuelle Stand des Imports kann über die API gerufen werden.

## Konfigurationsparameter

Alle Konfigurationsparameter beginnen mit `service.config`

| Name | Beschreibung                                      | Default |
| ---- |---------------------------------------------------| ------- |
| ungueltigewahlscheine.filenamesuffix | Dateinamenssuffix für die ungueltigen Wahlscheine | Ungueltigews.csv | 
| manual.filenamesuffix | Dateinamenssuffix für das Handbuch              | Handbuch.pdf | 
| async.corePoolSize | core Poolsize von ThreadPoolTaskExecutor        | 2 |
| async.maxPoolSize | max Poolsize von ThreadPoolTaskExecutor         | 2 |
| async.queueCapacity | Kapazität für Queue von ThreadPoolTaskExecutor  | 500 |
| async.threadNamePrefix | Prefix für Threads von  ThreadPoolTaskExecutor  | taskExecutor- |