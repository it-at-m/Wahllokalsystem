# Uppercase-Convention für Datenbanken

## Kontext

Aktuell sind sowohl Namen als auch Properties der Datenbanktabellen mal in Uppercase, mal in Lowercase.
Sofern Oracle und h2 nicht durch eine Änderung der migration-scripts auf Probleme stößt, wurde die Frage gestellt, die
Tabellennamen und Properties zu vereinheitlichen.

## Entscheidung

Nachdem ein Renaming der migration-scripts gemäß Recherche möglich ist, werden die Skripte aller Services refactored:
Syntax in Uppercase, Tabellenname, entsprechend der Klassenamen, in UpperCamelCase und die Properties in lowerCamelCase.

Beispiel:

```sql
CREATE TABLE Wahlbezirk
(
id VARCHAR2(36) NOT NULL,
wahlbezirkArt VARCHAR2(3) NOT NULL
)