# Security: H2-Konsole in Produktionsumgebung disablen

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Mit der H2-Konsole kann bei einem laufenden Service unter [service-url]:[service:port]/h2-console/ einfach auf die
H2-Datenbank zugegriffen werden. Dies stellt auf Produktivumgebungen im Allgemeinen ein Sicherheitsrisiko dar.

## Entscheidung

Die H2-Konsole wird mit `h2.console.enabled: true` ausschliesslich in den Application Profiles `db-h2` und `test`
aktiviert.

## Konsequenzen

### positiv

Die Sicherheitslücke in der Produktivumgebung ist geschlossen.

### negativ

Keine negativen Auswirkungen.