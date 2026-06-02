# Digitale Stimmzettel Erfassung

Das bisherige Verfahren zur Erfassung von Ergebnissen mittels Stapelbildung soll verbessert werden.

Mit den Stapeln waren diverse manuelle arbeiten notwendig die Zeit benötigen und potenzielle Fehlerquellen waren. Die
Erfassung der Stimmzettel verringert die Komplexität und die manuellen Schritte durch den Wahlvorstand. Die vorliegenden
Stimmzettel werden im System erfasst. Das System berechnet aus den erfassten Daten das Ergebnis.

Zur Migrationsbeiratswahl 2026 soll mit dem WLS eine erste Testversion der DSE ausprobiert werden.

## Prozess

```mermaid

flowchart TD
    S1[Stimmabgabe ist beendet]
    D2{UWB oder BWB?}
    S2a[Stimmabgabevermerke und Wahlscheine zählen]
    S2b[Wahlurne öffnen und Stimmettel zählen]
    S3[Stimmzettel vorbereiten]
    S4[Stimmzettel erfassen]
    S5[Stimmzettel ablegen]
    S6[Beschlüsse zu Stimmzetteln fassen]
    S7[Schnellmeldung ans Wahlamt übermitteln]
    S8[Niederschrift erstellen, übermitteln und übergeben]
    S9[Auszählung abschließen]

    S1-->D2
    D2-->|UWB| S2a
    D2-->|BWB| S2b
    S2a --> S3
    S2b --> S3
    S3 --> S4 --> S5 --> S6 --> S7 --> S8 --> S9
```

Mit dem Schritt `Stimmzettel vorbereiten` beginnt der neue Prozess. Mit dem Schritt `Schnellmeldung ans Wahlamt übermitteln`
ist man wieder im gewohnten Prozess.

Die separate Übermittlung einer Schnellmeldung und Niederschrift halt wahlrechtliche Gründe.

### Stimmzettel vorbereiten

In diesem Schritt erfolgen vorbereitenden Schritte, um die Erfassung zu erleichtern. Der Stimmzettel z.B. wird vollständig
entfaltet und es werden markieren vorgenommen, die Zeigen wo man stimmen findet.

### Stimmzettel erfassen

In diesem Schritt erfolgt die Erfassung der Stimmzettel im System. Stimmzettel über die der gesamte Wahlvorstand einen
Beschluss fassen muss werden entsprechend markiert.

### Stimmzettel ablegen

Die erfassten Stimmzettel werden entsprechend der Regel der Wahl für die Ablage vorbereitet. Stimmzettel, über die noch
ein Beschluss zu fassen ist, landen so auf einem separaten Stapel.

### Beschlüsse zu Stimmzetteln fassen

Der Wahlvorstand fasst die Beschlüsse die entsprechenden Stimmzettel

