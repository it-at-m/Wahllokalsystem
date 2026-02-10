# Geringe Anzahl an Renovate PRs zur gleichen Zeit

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Aktuell achtet Renovate darauf, dass maximal 10 PRs zur selben Zeit offen sind. Wir haben die Regel aktiv, dass ein 
Branch aktuell sein muss bevor er gemergt werden darf. Die PRs von Renovate werden aktuell gehalten. Wurde ein 
anderer PR gemergt, erfolgt ein Update des Branches via Force-Update. Für jeden neuen Push auf einen offenen PR 
werden die entsprechenden Actions ausgeführt. Mit unseren gut 12 Backend-Microservices führt ein PR zu 12 Jobs. Mit dem
Standardlimit von 10 Renovate-PRs kann dies bis zu 120 parallele Jobs führen. Diese werden aber nicht gleichzeit
abgearbeitet, ein Teil wird gequeued. 9 der 10 Jobs werden aber nicht direkt in einem Merge münden.

Das ganze führt dazu, dass man relativ lange warten muss um die Renovate-PRs abarbeiten zu können.

## Entscheidung

Die Menge an gleichzeitigen Renovate-PRs werden auf 5 limitiert.

## Konsequenzen

Renovate-PRs aus dem Bereich Security sind davon [nicht betroffen](https://docs.renovatebot.com/configuration-options/#prconcurrentlimit).

### positiv

Die einzelnen PRs können schneller bearbeitet werden.

### negativ

Renovate-PRs die sich gegenseitig bedingen, und daher im Build fehlschlagen, sind schwerer zu erkennen.
