# Immer vollständiger Migration nach Keycloak 

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Initiator für eine genauere Betrachtung von Keycloak und Keycloakmigration war die von renovate vorgeschlagene Hebung
von Version 20 auf 24. Diese wurde zuerst auch durchgeführt (siehe [PR - 241](https://github.com/it-at-m/Wahllokalsystem/pull/241)),
musste aber aufgrund von Inkompatibilitäten mit unseren Migrationsskripten wieder durch
[PR - 231](https://github.com/it-at-m/Wahllokalsystem/pull/231) zurückgenommen werden.
Zum Zeitpunkt der Rücknahme war nicht bekannt wie man mit den Userattributen, `wahlbezirk_art`, die wir einfügen wollten,
umgehen konnte. In diesem Zusammenhang viel auch auf, dass im Unterschied zur Version 20, mit der Version keine
Deltamigration mehr möglich war.

Es entstand das [Issue - 248](https://github.com/it-at-m/Wahllokalsystem/issues/248). Es wurde keine Lösung gefunden wie
über Keycloak-Migration die Userattribute pflegbar waren. Daher wurde das Issue geschlossen.

Später viel auf, dass die Version 20 von Keycloak und Keycloakmigration es nicht ermöglichte, die
Gruppenzuordnung einer Rolle zu entfernen ([Issue - 361](https://github.com/it-at-m/Wahllokalsystem/issues/361)).

Daher wurde das [Thema](https://github.com/it-at-m/Wahllokalsystem/issues/248) erneut eröffnet.

Die Beschreibung für den [Import](https://www.keycloak.org/server/containers#_importing_a_realm_on_startup) eines Realms
beim Start haben wir später gefunden. Die Option `Unmanaged Attributes` in den Realmsettings, ermöglichte es Userattribute
zu verwenden.

## Entscheidung

Wir verwenden Keycloak in Version 25 und Migrationen nach Keycloak werden immer vollständig durchgeführt.
Es muss keine Delta-Migration geben.

Dadurch das Keycloak zeitnah durch unseren Authservice abgelößt wird, überwog der *KISS*-Aspekt. Anstelle einer
umfangreichen Featurelist konzentrieren wir uns auf wenige notwendige Arten von API-Calls durch Keycloakmigration.

Unterstützt wird die Entscheidung gegen die Option einer Delta-Migration von unserem
Nutzungsverhalten. Wir wechseln häufig zwischen verschiedenen Feature-Branches. Die jeweiligen Branches haben
Migrationsfiles die der andere Branch nicht hat. Daher führen wir meist eine vollständige Migration durch.

## Konsequenzen

### positiv

Es ist kein Zusatzwissen oder zusätzlichen Tätigkeiten beim Erstellen des JSON-Files für den Import erforderlich. Wenn
man eine Deltamigration ermöglichen will, muss man manuell zu den exportieren Daten aus Keycloak Rollen und Benutzer hinzufügen.

Wir benötigen nur einen kleinen Teil der Funktionen von keycloakmigration. Aufgrund der Erfahrungen mit nicht
funktionierenden Features ist somit das Risiko geringer, dass wir in einen Fehler laufen.

Mit der Fokussierung auf eine vollständige Migration ist es möglich die Keycloak-Files überschaubarer anzuordnen.

### negativ

Es muss immer eine vollständige Migration erfolgen. Eine Teilmigration ist nicht möglich. 

