# Vergabe von Stimmen auf dem Stimmzettel

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Der Wähler vergibt seine Stimmen auf dem Stimmzettel. Dies wird in der Anwendung durch eine Tastatureingabe ermöglicht.  
Damit das möglichst nachvollziehbar für die User ist, werden folgende Regeln definiert.

## Entscheidung

### Einzelstimmenvergabe

Bekommt ein Kandidat, der bereits eine Einzelstimme hat, erneut eine Einzelstimme vergeben, so wird diese zur bestehenden
Stimme dazuaddiert.

Hat ein Kandidat mehrere Nennungen und die zweite Nennung hat zum Beispiel bereits eine Stimme, so führt die Eingabe
einer Einzelstimme bei diesem Kandidaten zu einer weiteren Stimme bei der zweiten Nennung.

Hat ein Kandidat noch keine Einzelstimmen erhalten, wird eine Stimme auf die erste freie Nennung vergeben. Gibt es keine freie Nennung
mehr, bekommt der User einen Hinweis, dass keine Stimme vergeben werden kann.
Hat ein Kandidat zum Beispiel drei Nennungen und die erste wurde zuvor gestrichen, wird auf die zweite Nennung eine Stimme vergeben.

### Vergabe von Listenkreuzen

Die Vergabe von Listenkreuzen setzt den Zustand bei einem Wahlvorschlag. Ist bereits ein Listenkreuz gesetzt,
bleibt dieses erhalten. Ist kein Listenkreuz gesetzt, wird ein Listenkreuz gesetzt.

### Streichungen von Kandidaten

Soll ein Kandidat über die Tastatur gestrichen werden, wird die erste freie Nennung ohne Einzelstimme oder Streichung gestrichen.
Gibt es keine freie Nennung, bekommt der User einen Hinweis, dass keine Streichung durchgeführt werden kann.

Bei einem Kandidaten mit drei Nennungen, bei dem die erste Nennung bereits eine Einzelstimme hat und die zweite Nennung
gestrichen wurde, führt die Eingabe des Befehls zur Vergabe einer Streichung bei der dritten Nennung.

## Konsequenzen

### positiv

### negativ

Eine Verteilung von Einzelstimmen auf verschiedene Nennungen eines Kandidaten erfordert mehr Aufwand.
