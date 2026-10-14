# Health-Checks ohne DB- und LDAP-Verbindung

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Unsere Services werden über Health-Checks überwacht. Bisher flossen dabei auch die Verfügbarkeit der Datenbank und des LDAP in den allgemeinen Health-Status ein. Das führte in der Praxis zu folgenden Problemen:

- Ist die Datenbank selbst gestört (Down/Netzwerkproblem) oder ist der Connection-Pool durch Last erschöpft, werden Instanzen als "unhealthy" entfernt/neu gestartet. Das behebt die eigentliche Ursache nicht und verschärft Lastspitzen (zusätzliche Reconnects, erhöhte Last auf noch funktionierenden Instanzen).
- Bei LDAP-Störungen ist primär der Login betroffen. Ein Ausfall des Auth-Services aufgrund eines fehlgeschlagenen LDAP-Health-Checks führt jedoch dazu, dass keine Services mehr Berechtigungsinformationen erhalten können – ein systemweiter Ausfall kann die Folge sein.

Um diese negativen Effekte zu vermeiden, sollen DB- und LDAP-Konnektivität nicht länger den allgemeinen Health-Status bestimmen.

## Entscheidung

- In allen Services wird der Datenbank-Health-Check deaktiviert: `management.health.db.enabled: false`.
- Im Auth-Service wird zusätzlich der LDAP-Health-Check deaktiviert: `management.health.ldap.enabled: false`.

Die übrigen Health-/Probe-Indikatoren (z. B. Liveness/Readiness, Info, Prometheus) bleiben unverändert aktiv.

## Konsequenzen

### Positiv

- Verhindert unnötige Restarts bei externen Infrastrukturproblemen (DB/LDAP).
- Erhöht die Stabilität des Gesamtsystems während teilweiser Infrastrukturstörungen.

### Negativ

- Der Health-Status der Services spiegelt DB-/LDAP-Verfügbarkeit nicht mehr wider. Diese muss über dedizierte Infrastruktur-Monitoring-Lösungen (Datenbank-/LDAP-Monitoring) bzw. anwendungsspezifische Readiness-/Business-Checks beobachtet werden.
- Fehlerbilder durch DB-/LDAP-Ausfälle werden nicht mehr automatisch durch Restart-Strategien auf Service-Ebene adressiert und benötigen operative Gegenmaßnahmen.
