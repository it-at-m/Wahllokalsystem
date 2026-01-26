# Abweichung vom Renovate-Scheduler der RefArch

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Issue [Fehlende Renovate PRs #2255](https://github.com/it-at-m/Wahllokalsystem/issues/2255)

## Entscheidung

Am Montag darf Renovate zu jeder Uhrzeit einen PR erstellen. In Verbindung mit dem ADR
[Geringe Anzahl an Renovate PRs zur gleichen Zeit](/technik/adr/adr-lower-number-of-renovate-prs) sollten wir trotzdem
nicht von Pull-Requests überflutet werden.

## Konsequenzen

Wenn wir am Montag Renovate-PRs abschließen kann es sein dass neue erstellt werden, sollte Renovate am Montag erneut
scannen.

### positiv

Mit dem Fix des Bugs wollen wir erreichen, dass das LCM durch die PRs wieder mehr in den Fokus wandert. Eine Auflistung
der möglichen PRs im [Renovate-Dashboard](https://github.com/it-at-m/Wahllokalsystem/issues/1) ist nicht ausreichend.
