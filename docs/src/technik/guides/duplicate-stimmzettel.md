# Duplizieren von Stimmzetteln

## Ausgangslage

Zum Testen, insbesondere der Performance, ist es wichtig, eine größere Menge von Stimmzetteln zu haben. Um diese nicht alle
manuell erfassen zu müssen, wurde ein DB-Skript (Stored Procedure) erstellt, mit dem man einen Stimmzettel n-fach
duplizieren kann.

## Installation

Das Skript `createStoredProcedureToGenerateUUIDv4.sql` sowie `createStoreProcedureForStimmzettelDuplication.sql`
(aus `wls-ergebnismeldung-service\test\resources\db\oracle`)
müssen in der Umgebung und im Schema des Ergebnismeldungsservices ausgeführt werden,
in dem die Funktionalität zur Verfügung stehen soll.

## Ausführung

Um einen Stimmzettel n-fach zu duplizieren, muss das folgende Statement ausgeführt werden:

```sql
CALL duplicate_stimmzettel('wahlID', 'wahlbezirkID', 'teamID', stimmzettelkennung, n);
```

Die Parameter `wahlID`, `wahlbezirkID`, `teamID` und `stimmzettelkennung` beschreiben den Stimmzettel, der
dupliziert werden soll. Der Parameter `n` gibt an, wie viele Duplikate erzeugt werden sollen.
Dabei werden die Wahlvorschläge, Kandidaten und Beschlüsse entsprechend dupliziert.

> [!NOTE]
> Die duplizierten Stimmzettel werden am Ende, also nach dem höchsten Wert der `stimmzettelkennung` des Teams, eingefügt.

> [!NOTE] BrowserCache (IndexedDB leeren)
> Ist der Service-Worker aktiv wird es notwendig sein, die IndexedDB zu leeren damit die duplizierten Stimmzettel
> sichtbar werden. Eine Leerung erfolgt auch im Rahmen eines Benutzerwechsels.

## Disclaimer zu erzeugten IDs

Die UUIDs, die mit dem Skript erstellt werden, verwenden einen anderen Algorithmus als der Service. Es ist daher
möglich, dass im Laufe der Zeit Konflikte auftreten können.
