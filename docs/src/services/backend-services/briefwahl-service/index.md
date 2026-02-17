# Briefwahl-Service

In diesem Service geht es um spezielle Themen im Bereich Briefwahl. Daten und Funktionalitäten,
die allein für die Briefwahl von Bedeutung sind und nicht durch die Funktionalität der anderen Services abgedeckt sind,
stehen hier zur Verfügung.

## Abhängigkeiten

Der Service hat keine Abhängigkeiten zu anderen Services.

## Daten und Funktionen

### Wahlbriefdaten

Wahlbriefdaten umfassen Informationen über die Menge an Wahlbriefen sowie Nachträgen.

### Beanstandete Wahlbriefe

Beanstandete Wahlbriefe sind Wahlscheine und Stimmzettelumschläge, welche nach Erhalt genauer betrachtet wurden,
weil sie nicht zweifelsfrei gültig sind. Ist der Beschluss dann nicht `Zugelassen`, kann es aus folgenden Gründen zu
Zurückweisungen kommen:

| Wahlscheine                       | Stimmzettelumschlag                         |
|-----------------------------------|---------------------------------------------|
| Wahlschein ungültig laut Liste    | Stimmzettelumschlag fehlt                   |
| Kein Original-Wahlschein          | Lose Stimmzettel                            |
| Unterschrift auf Wahlschein fehlt | Wahlbrief und Stimmzettelumschlag offen     |
|                                   | Wahlscheine ungleich Stimmzettelumschläge   |
|                                   | Nicht-amtlicher Stimmzettelumschlag         |
|                                   | Stimmzettelumschlag gefährdet Wahlgeheimnis |
|                                   | Gegenstand im Stimmzettelumschlag           |
|                                   | Für diese Wahl nicht wahlberechtigt         |
