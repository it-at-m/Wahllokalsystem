# Digitale Stimmzettel Erfassung (DSE)

Das bisherige Verfahren zur Erfassung von Ergebnissen mittels Stapelbildung soll verbessert werden.

Mit den Stapeln waren diverse manuelle Arbeiten notwendig, die Zeit in Anspruch nehmen und potenzielle Fehlerquellen darstellen. Die
digitale Erfassung der Stimmzettel verringert die Komplexität und die manuellen Schritte durch den Wahlvorstand. Die vorliegenden
Stimmzettel werden im System erfasst. Das System berechnet aus den erfassten Daten das Ergebnis.

Zur Migrationsbeiratswahl 2026 soll mit dem WLS eine erste Testversion der DSE ausprobiert werden.

## Prozess

```mermaid
flowchart TD    
    S1[Stimmabgabe ist beendet]
    D2{UWB oder BWB?}
    S2a[Stimmabgabevermerke und Wahlscheine zählen]
    S2b[Wahlurne öffnen und Stimmzettel zählen]
    S3["(1) Stimmzettel vorbereiten"]
    S4["(2) Stimmzettel erfassen"]
    S5["(3) Stimmzettel ablegen"]
    S6["(4) Beschlüsse zu Stimmzetteln fassen"]
    S7[Schnellmeldung ans Wahlamt übermitteln]
    S8[Niederschrift erstellen, übermitteln und übergeben]
    S9[Auszählung abschließen]

    S1-->D2
    D2-->|UWB| S2a
    D2-->|BWB| S2b
    S2a --> S3
    S2b --> S3
    S3 --> S4 --> S5 --> S6 --> S7 --> S8 --> S9

    class S3,S4,S5,S6 newProcessStep;
```

Mit dem Schritt `Stimmzettel vorbereiten` beginnt der neue Prozess. Mit dem Schritt `Schnellmeldung ans Wahlamt übermitteln`
ist man wieder im gewohnten Prozess.

Die separate Übermittlung einer Schnellmeldung und Niederschrift hat wahlrechtliche Gründe.

### Neuer Prozess zur DSE

#### (1) Stimmzettel vorbereiten

In diesem Schritt erfolgen vorbereitende Maßnahmen, um die Erfassung zu erleichtern. Der Stimmzettel wird z.B. vollständig
entfaltet, und es werden Markierungen vorgenommen, die zeigen, wo man Stimmen findet.

#### (2) Stimmzettel erfassen {#stimmzettel-erfassen}

In diesem Schritt erfolgt die Erfassung der Stimmzettel im System. Der Stimmzettelerfassungs-Workflow-Status wird
auf `(e) STE in Bearbeitung` gesetzt. Stimmzettel, über die der gesamte Wahlvorstand einen Beschluss fassen muss,
werden entsprechend markiert und mit einem oder mehreren `Vormerkungsgründen` versehen.

Um eine möglichst intuitive Erfassung der Stimmen des Stimmzettels zu ermöglichen, haben wir
[Regeln](/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen) definiert.

Zur schnelleren Erfassung können spezielle [Kurzbefehle](#kurzbefehle-fur-die-erfassung) verwendet werden.

#### (3) Stimmzettel ablegen

Die erfassten Stimmzettel werden entsprechend der Regeln der Wahl für die Ablage vorbereitet. Stimmzettel, über die noch
ein Beschluss zu fassen ist, landen so auf einem separaten Stapel.

#### (4) Beschlüsse zu Stimmzetteln fassen {#beschluesse-erfassen}

Sind alle Teams mit der Erfassung fertig und der Stimmzettelerfassungs-Workflow-Status `(f) STE abgeschlossen` wurde
erfolgreich übermittelt, startet die Beschlussdokumentation. Der Wahlvorstand fasst die Beschlüsse zu den zuvor
markierten Stimmzetteln und überträgt die Ergebnisse ins System. Eine Zusammenfassung mehrerer
`Vormerkungsgründe` stellt einen `Beschlussvorschlag` dar, über welchen im Gremium abgestimmt wird. Der Vorschlag,
der am Ende die Abstimmung gewinnt, wird als `Entscheidungsgrund` gespeichert. Sind alle Beschlüsse vollständig
dokumentiert, wird der Stimmzettelerfassungs-Workflow-Status `(g) BF abgeschlossen` gespeichert.

### Neue Statuswerte

Mit dem neuen Prozess wurden neue Statuswerte für die einzelnen Erfassungsteams sowie Wahlbezirke eingeführt:

```mermaid
stateDiagram-v2 
    direction LR
    
    state "(a) registriert" as Registriert
    state "(b) in Bearbeitung" as InBearbeitung
    state "(c) unterbrochen" as Unterbrochen
    state "(d) abgeschlossen" as Abgeschlossen
    state "(e) STE in Bearbeitung" as STEInBearbeitung
    state "(f) STE abgeschlossen" as STEAbgeschlossen
    state "(g) BF abgeschlossen" as BFAbgeschlossen
    state "(h) NS gedruckt" as NSGedruckt
    
    state Stimmzettelerfassungs-Workflow-Status {
        [*] --> STEInBearbeitung
        STEAbgeschlossen --> BFAbgeschlossen
        STEAbgeschlossen --> STEInBearbeitung
        STEInBearbeitung --> STEAbgeschlossen
        InBearbeitung --> STEInBearbeitung
        STEAbgeschlossen --> InBearbeitung
        BFAbgeschlossen --> InBearbeitung 
        BFAbgeschlossen --> NSGedruckt
        Abgeschlossen --> NSGedruckt
        NSGedruckt --> [*]
    }
    
    state Stimmzettelerfassungs-Team-Status {
        [*] --> Registriert
        Registriert --> InBearbeitung
        InBearbeitung --> Abgeschlossen
        Registriert --> Abgeschlossen
        InBearbeitung --> Unterbrochen
        Unterbrochen --> InBearbeitung
        Unterbrochen --> Abgeschlossen
    }
      
    classDef steWfStatus fill:#faca7d
    classDef steStatus fill:#997fba
    
    class STEInBearbeitung,STEAbgeschlossen,BFAbgeschlossen,NSGedruckt steWfStatus
    class Registriert,InBearbeitung,Unterbrochen,Abgeschlossen steStatus
    
```

```mermaid
stateDiagram-v2
    direction LR
    state "STE = Stimmzettelerfassung | BF = Beschlussfassung | NS = Niederschrift" as Legende  
    classDef text fill:#ffffff,font-style:italic,stroke:#4f4f4f
    class Legende text
```

| Status                   | Beschreibung, wann der Status gesetzt wird                                                                                                                                                                                                                                                                                                                                                                                                    |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `(a) registriert`        | Sobald sich ein Erfassungsteam im System eingeloggt hat, ist es `registriert`.                                                                                                                                                                                                                                                                                                                                                                |
| `(b) in Bearbeitung`     | Die Stimzettelerfassung [(2)](#stimmzettel-erfassen) kann von Teams mit der Rolle **Erfassungsteam** gestartet werden, sobald sie `(a) registriert` sind, und von Teams mit der Rolle **Schriftführung**, sobald zuvor alle Stimmzettel und Wahlscheine gezählt wurden. Hat ein Team die Erfassung bereits `(d) abgeschlossen`, kann es nur durch ein Team mit der Rolle **Schriftführung** wieder für die Bearbeitung freigeschalten werden. |
| `(c) unterbrochen`       | Jedes Team kann die Stimzettelerfassung [(2)](#stimmzettel-erfassen) kurzzeitig `(c) unterbrechen`, zum Beispiel für die Situation, dass die Erfassung an einem Abend beendet und am nächsten Morgen wieder aufgenommen wird. Der Stimmzettelerfassungs-Workflow-Status bleibt dabei auf `(e) STE in Bearbeitung`.                                                                                                                            |
| `(d) abgeschlossen`      | Sobald alle Stimmzettel aus dem Vorrat eines Teams erfasst wurden, wird das durch den Status `(d) abgeschlossen` bestätigt. Sollte ein eingeloggtes Team keine Stimmzettel zu erfassen haben, kann auch ein Statuswechsel von `(a) registriert` zu `(d) abgeschlossen` erfolgen.                                                                                                                                                              |
| `(e) STE in Bearbeitung` | Sobald ein Team eines Wahlbezirks (egal welche Rolle) einen Statuswechsel zu `(b) in Berabeitung` vollzieht, wird im Hintergrund geprüft, dass der Status `(e) STE in Bearbeitung` auch für den Wahlbezirk gesetzt ist. Dies ist zum Beispiel der Fall, wenn das erste Team mit der Bearbeitung der Stimmzettelerfassung beginnt, oder wenn nach Abschluss der Erfassung ein oder mehrere Teams wieder freigeschalten werden.                 |
| `(f) STE abgeschlossen`  | Der Stimmzettelerfassungs-Workflow-Status `(f) STE abgeschlossen` wird von Teams mit der Rolle **Schriftführung** gesetzt, wenn alle Teams eines Wahlbezirks die Erfassung `(d) abgeschlossen` haben und bestätigt wurde, dass es keine übrigen Stimmzettel mehr im Erfassungsvorrat gibt.                                                                                                                                                    |
| `(g) BF abgeschlossen`   | Nach der Dokumentation aller Beschlussergebnisse [(4)](#beschluesse-erfassen) bestätigen die Teams mit der Rolle **Schriftführung**, dass die `(g) BF abgeschlossen` ist.                                                                                                                                                                                                                                                                     |
| `(h) NS gedruckt`        | Sobald die Teams mit der Rolle **Schriftführung** die Niederschrift gedruckt haben, wird der Stimmzettelerfassungs-Workflow-Status `(h) NS gedruckt` gesetzt.                                                                                                                                                                                                                                                                                 |

> [!NOTE]
> Sollte kein Team einen Stimmzettel erfasst haben, wird der Status `(e) STE in Bearbeitung` übersprungen.

## Kurzbefehle für die Erfassung {#kurzbefehle-fur-die-erfassung}

Um eine schnelle Erfassung der Daten des Stimmzettels zu ermöglichen, können Befehle eingegeben werden. Die Anwendung
gibt Feedback, wenn der Befehl nicht ausführbar oder falsch war.

### Input-Handling Architektur

```mermaid
flowchart LR
    A["Start: User-Eingabestring"] --> B{"`_commandHandler.canHandle(command)_ <br/><br/> technische Prüfung des Befehls: <br/> Eingabe ist korrekt und kann verarbeitet werden?`"}

    B -->|nein| C{"Weitere handler vorhanden ?"}
    
    C -->|"ja (try next handler)"| B
    C -->|"nein (command not found)"| D["throw <br/> UnsupportedCommandError"]
    
    B -->|ja| F{"`_commandHandler.handleOrThrow(...)_ <br/><br/> fachliche Prüfung des Befehls: <br/> Kandidat/Wahlvorschlag existiert und Änderung erlaubt?`"}

    F -->|ja| H["Anpassung Datenmodell + <br/> Update der Eingabehistorie"]
    F -->|nein| J["throw <br/> CommandExecutionError"]
```

### Befehle

| Befehl                                | Funktion                                                                                                                                                        | Beispiel  |
|---------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|
| &lt;Kandidatordnungszahl>             | Fügt eine Stimme bei dem/der Kandidat\*in mit der `Ordnungszahl` hinzu                                                                                          | 101       |
| &lt;Kandidatordnungszahl>+            | Fügt eine Stimme bei dem/der Kandidat\*in mit der `Ordnungszahl` hinzu                                                                                          | 101+      |
| &lt;Kandidatordnungszahl>+&lt;n>      | Fügt `n` Stimmen bei dem/der Kandidat\*in mit der `Ordnungszahl` hinzu                                                                                          | 101+3     |
| &lt;Kandidatordnungszahl>-            | Entfernt eine Stimme bei dem/der Kandidat\*in mit der `Ordnungszahl`                                                                                            | 101-      |
| &lt;Kandidatordnungszahl>-&lt;n>      | Entfernt `n` Stimmen bei dem/der Kandidat\*in mit der `Ordnungszahl`                                                                                            | 101-3     |
| [u/U]&lt;Kandidatordnungszahl>        | Fügt 1 ungültige Stimme bei dem/der Kandidat\*in mit der `Ordnungszahl` hinzu                                                                                   | u101      |
| [u/U]&lt;Kandidatordnungszahl>+&lt;n> | Fügt `n` ungültige Stimmen bei dem/der Kandidat\*in mit der `Ordnungszahl` hinzu                                                                                | u101+3    |
| [u/U]&lt;Kandidatordnungszahl>-       | Entfernt 1 ungültige Stimme bei dem/der Kandidat\*in mit der `Ordnungszahl`                                                                                     | u101-     |
| [u/U]&lt;Kandidatordnungszahl>-&lt;n> | Entfernt `n` ungültige Stimmen bei dem/der Kandidat\*in mit der `Ordnungszahl`                                                                                  | u101-3    |
| &lt;untere>-&lt;obere>                | Fügt je 1 Stimme bei allen Kandidat\*innen im Bereich der `Ordnungszahl` von `untere`–`obere` hinzu                                                             | 501-510   |
| &lt;untere>-&lt;obere>+&lt;n>         | Fügt je `n` Stimmen im Bereich der `Ordnungszahl` von `untere`–`obere` hinzu                                                                                    | 527-535+2 |
| &lt;Wahlvorschlagsnummer>             | Kennzeichnet den Wahlvorschlag mit `Ordnungszahl` (Die Eingabe erfolgt entweder als Wahlvorschlagsnummer oder als Ordnungszahl mit „00“ am Ende)                | 5, 500    |
| &lt;Wahlvorschlagsnummer>-            | Entfernt die Kennzeichnung des Wahlvorschlags mit `Ordnungszahl` (Die Eingabe erfolgt entweder als Wahlvorschlagsnummer oder als Ordnungszahl mit „00“ am Ende) | 5-, 500-  |
| [s/S]&lt;Kandidatordnungszahl>        | Streichung für den/die Kandidat\*in mit der `Ordnungszahl`                                                                                                      | s501      |
| [s/S]&lt;untere>-&lt;obere>           | Streichungen für alle Kandidat\*innen im Bereich der `Ordnungszahl` von `untere`–`obere`                                                                        | s501-509  |
| [s/S]&lt;Kandidatordnungszahl>-       | Streichung für den/die Kandidat\*in mit der `Ordnungszahl` wird zurückgenommen                                                                                  | s501-     |
| [s/S]&lt;untere>-&lt;obere>-          | Streichungen für alle Kandidat\*innen im Bereich der `Ordnungszahl` von `untere`–`obere` werden zurückgenommen                                                  | s501-509- |

### Reststimmenvergabe

Wenn bei einem Wahlvorschlag ein Listenkreuz gesetzt wird, werden Stimmen über die sogenannte Reststimmenvergabe verteilt.
Dabei werden die Stimmen der Reihe nach auf die Kandidaten des Wahlvorschlages verteilt,
welche sonst noch keine Kennzeichen (Stimmen oder Streichung) erhalten haben.

Grundsätzlich gilt, dass die Vergabe nur erfolgt, wenn eine eindeutige Vergabe möglich ist. Es können nur so viele Stimmen
vergeben werden, wie nicht explizit durch den/die Wähler\*in vergeben wurde. Die Menge der bereits vergebenen Stimmen
ist die Summe aus den vergebenen Einzelstimmen, sowie sonstiger ungültiger Stimmen.

> [!NOTE] Beispiel - Reststimmenmenge bei vorhandenen Einzelstimmen
> Bei einer Wahl können bis zu 40 Stimmen vergeben werden. Der/die Wähler\*in hat 3 Kreuze bei unterschiedlichen
> Kandidat\*innen gesetzt und bei einer/einem Weiteren eine 2 eingetragen.
>
> Somit sind 5 Stimmen vergeben und für die
> Reststimmenvergabe stehen noch 35 Stimmen zur Verfügung.

> [!NOTE] Beispiel - unerlaubterweise wurde ein/eine zusätzliche/r Kandidat\*in ergänzt
> Bei einer Wahl können bis zu 40 Stimmen vergeben werden. Der/die Wähler\*in hat 3 Kreuze bei unterschiedlichen
> Kandidat\*innen gesetzt. Zusätzlich wurde ein/eine weiter/e Kandidat\*in ergänzt mit der Zahl 3 davor.
> Somit sind 6 Stimmen vergeben, wobei 3 gültig sind, und 3 ungültig sind.
>
> Für die Reststimmenvergabe
> stehen noch 34 Stimmen zur Verfügung.

> [!NOTE] Beispiel - Reststimmenmenge bei zu vielen Einzelstimmen bei einem Kandidaten
> Bei einer Wahl können bis zu 40 Stimmen vergeben werden. Je Kandidat\*in dürfen maximal 3 Stimmen vergeben werden.
> Der/die Wähler\*in hat bei einem/einer Kandidat\*in ein 5 eingetragen.
> Somit sind 5 Stimmen vergeben, wobei 3 gültig sind, und 2 ungültig sind.
>
> Für die Reststimmenvergabe
> stehen noch 35 Stimmen zur Verfügung.

> [!NOTE] Beispiel - keine Reststimmenvergabe möglich
> Bei einer Wahl können bis zu 40 Stimmen vergeben werden. Der/die Wähler\*in hat bei 2 Wahlvorschlägen jeweils das
> Listenkreuz gesetzt. Je Wahlvorschlag gibt 40 Kandidat\*innen.
>
> Für eine Reststimmenvergabe wären 80 Stimmen notwendig.
> Somit ist nicht eindeutig erkennbar wie die Aufteilung der 40 Stimmen auf die 80 Kandidat\*innen erfolgen soll.

Die Vergabe der Reststimmen erfolgt entsprechend der Listenposition innerhalb des Wahlvorschlages beginnend bei der ersten
Position. Ein/eine Kandidat\*in kann nur Reststimmen bekommen, wenn er/sie noch keine Stimmen erhalten haben,
oder sie gestrichen wurden. Gibt es für Kandidat\*innen mehrere Nennungen, kann ein/eine Kandidat\*in des Weiteren
nur Reststimmen erhalten wie er/sie nicht bereits die maximal erlaubte Menge an Stimmen erhalten hat.

> [!NOTE] Beispiel - Reststimmenvergabe bei vorhandener Einzelstimmenvergabe
> Es gibt die Listenpositionen 1 bis 10. Die Positionen 1 bis 3 und Position 6 haben bereits Einzelstimmen erhalten.
>
> Über die Reststimmenvergabe bekommen die Positionen 4, 7, 8, 9 und 10 jeweils eine Reststimme.

> [!NOTE] Beispiel - Reststimmenvergabe bei vorhandener Einzelstimmenvergabe und Streichung
> Es gibt die Listenpositionen 1 bis 10. Die Positionen 1 bis 3 haben bereits Einzelstimmen erhalten. Die Position 6
> wurde gestrichen.
>
> Über die Reststimmenvergabe bekommen die Positionen 4, 7, 8, 9 und 10 jeweils eine Reststimme.

> [!NOTE] Beispiel - Reststimmenvergabe bei vorhandener Mehrfachnennung
> Es gibt die Listenpositionen 1 bis 10, jeweils in dreifacher Nennung. Je Kandidat\*in dürfen maximal 3 Stimmen
> vergeben werden.
>
> Die Position 1 hat bei jeder der 3 Nennung ein Kreuz.  
> Die Position 2 hat bei der ersten Nennung eine 2 stehen.  
> Die Position 3 hat bei den ersten beiden Nennungen eine 2 stehen.  
> Bei Position 4 wurde die erste Nennung gestrichen.
>
> Über die Reststimmenvergabe bekommen die Position 1 keine Stimmen, weil für den/die Kandidat\*in bereits die maximale
> Menge an Stimmen erreicht ist.  
> Die Position 2 bekommt noch eine Reststimme womit sie das Maximum an Stimmen erreicht.  
> Die Position 3 bekommt keine Reststimmen, weil für den/die Kandidat\*in bereits die maximale
> Menge an Stimmen überschritten hat. Eine der 4 Stimmen wird als ungültig gewertet.  
> Die Position 4 bekommt noch 2 Reststimmen. Für die gestrichene Nennung gibt es keine Reststimme.  
> Die restlichen Positionen bekommen jeweils 3 Reststimmen.
