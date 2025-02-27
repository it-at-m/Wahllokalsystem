# Namingconventions für Tests

Für Tests, Frontend als auch Backend, gelten prinzipiell die gleichen Regeln, welche aber unterschiedlich umzusetzen sind.

## Entscheidung

Die Bezeichnungen sollen dem Schema `should_<result>_when_<input>` folgen, wobei Result (= ExpectedBehavior, bzw.
erwartetes Ergebnis) und Input (= StateUnderTest, bzw. zu testender Zustand) in CamelCase gehalten werden. Dem Schema
entsprechend sind die Testnamen auch auf Englisch zu formulieren.

Wir haben uns darauf geeinigt, die zu testenden Methoden im Backend mit `@Nested`, und im Frontend mit
`describe("xyz", () => {}` zu gruppieren. Im Fall von überladenen Methoden werden diese innerhalb der Methodenklasse
zusätzlich verschachtelt und ebenfalls mit `@Nested` annotiert, oder in einen neuen `describe()`-Block eingeordnet.

## Kontext

Aktuell gibt es keine Struktur oder Vorgaben bei der Benennung von Tests. Die meisten Namen sind sehr kurz gehalten und
wenig aussagekräftig, wie zum Beispiel:

````java
void dataFound() {}
void noDataFound() {}
void serviceCalled() {}
````

Damit der Gesamtcode im Projekt übersichtlicher und einheitlicher ist, sollen Naming Conventions eingesetzt werden.
Grundlage für die Einführung sind unter anderem auch sich wiederholende Tests mit gleichem Inhalt in den verschiedenen
Services. So wird gewährleistet, dass deren Kontext schneller klar ist, ohne den Code lesen zu müssen und die Wartung
und Erweiterung des Codes wird erleichtert.