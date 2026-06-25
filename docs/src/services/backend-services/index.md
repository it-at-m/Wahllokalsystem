# Backend-Services

Für jeden Service gibt es eine separate Beschreibung zu dessen Aufgaben und Funktionen.

## Fachliches Datenmodell

Das Datenmodell wurde unter Berücksichtigung der gesetzlichen Grundlagen verschiedener Wahlen und Abstimmungen sowie
des örtlichen Kommunal- und Landeswahlrechts erstellt.
Es verwendet bewusst übergreifende Begriffe, damit unterschiedliche Wahlarten
in einer gemeinsamen Struktur abgebildet werden können.

![Datenmodell:](/fachlichesDatenmodell/vermutetesFachlichesDatenmodell_20241008.png)
*__Abbildung 1:__ Fachliches Datenmodell*

### Gegenstand und Abgrenzung

Das Modell beschreibt die fachlichen Beziehungen zwischen Wahlterminen, einzelnen Wahlen oder Abstimmungen,
organisatorischen Wahlbezirken, Stimmzettelgebieten sowie den auf dem Stimmzettel enthaltenen Wahlvorschlägen oder Abstimmungsvorlagen.

### Erläuterung der bestehenden Entitäten

| Entität                 | Bedeutung im fachlichen Datenmodell                                                                                                                                                                                                                  |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Wahltermin`            | Organisatorischer Rahmen für eine oder mehrere gleichzeitig oder organisatorisch gemeinsam durchgeführte Wahlen und Abstimmungen. Ein Wahltermin ist einem Datum zugeordnet. Die Ergebnisermittlung kann über den Wahltag hinaus fortgesetzt werden. |
| `Wahl`                  | Eine einzelne Wahl oder Abstimmung innerhalb eines Wahltermins, zum Beispiel Europawahl, Bundestagswahl, Stadtratswahl oder Bürgerentscheid. Die Wahlart wird als Eigenschaft dieser Entität geführt.                                                |
| `Stimmzettelgebiet`     | Gebiet, innerhalb dessen bei einer bestimmten Wahl oder Abstimmung Stimmzettel mit demselben Inhalt verwendet werden. Die konkrete räumliche Bedeutung hängt von der Wahlart ab.                                                                     |
| `Wahlbezirk`            | Allgemeine organisatorische Einheit, in der Stimmen abgegeben oder ausgezählt werden und für die ein Ergebnis ermittelt wird. Der Begriff wird im Modell als Oberbegriff für Stimm-/Wahlbezirke und Briefwahlbezirke verwendet.                      |
| `Wahlvorstand`          | Für einen Wahlbezirk zuständiges Wahlorgan. Je nach Art des Wahlbezirks handelt es sich um einen Wahlvorstand oder einen Briefwahlvorstand.                                                                                                          |
| `Wahlvorstandsmitglied` | Mitglied eines Wahlvorstands oder Briefwahlvorstands.                                                                                                                                                                                                |
| `Vorschlag/Vorlage`     | Gemeinsame Oberklasse für Wahlvorschläge bei Wahlen und Abstimmungsvorlagen bei Volks- oder Bürgerentscheiden.                                                                                                                                       |
| `Wahlvorschlag`         | Zur Wahl stehender Vorschlag. Je nach Wahlart kann er beispielsweise eine Liste oder eine einzelne Person umfassen. Der Wahlvorschlag ist nicht zwingend mit einer Partei gleichzusetzen.                                                            |
| `Kandidat`              | Bewerberin oder Bewerber innerhalb eines Wahlvorschlags. Im Modell wird zwischen Direktkandidaten und Listenkandidaten unterschieden.                                                                                                                |
| `Direktkandidat`        | Bewerberin oder Bewerber, die beziehungsweise der unmittelbar gewählt werden kann, zum Beispiel bei der Oberbürgermeisterwahl oder als Wahlkreisbewerbung bei der Bundestagswahl.                                                                    |
| `Listenkandidat`        | Bewerberin oder Bewerber innerhalb einer Liste, zum Beispiel bei der Stadtratswahl oder der Bezirksausschusswahl.                                                                                                                                    |
| `ReferendumVorlage`     | Bei einem Volksentscheid oder Bürgerentscheid zur Abstimmung gestellte Frage beziehungsweise Vorlage.                                                                                                                                                |
| `ReferendumOption`      | Zu einer Referendumsvorlage auswählbare Antwortmöglichkeit, regelmäßig „Ja“ oder „Nein“. Bei mehreren Vorlagen oder einer Stichfrage werden mehrere Vorlagen mit ihren jeweiligen Optionen abgebildet.                                               |

### Zuordnung ergänzender organisatorischer Begriffe

Für die Beschreibung der praktischen Wahlorganisation sind weitere Begriffe erforderlich. Sie führen jedoch nicht zwingend zu zusätzlichen Entitäten im dargestellten Teilmodell. Die nachfolgende Zuordnung stellt klar, wie diese Begriffe einzuordnen sind.

| Begriff                  | Zuordnung zum bestehenden Modell                                                           | Erläuterung                                                                                                                                                                |
|--------------------------|--------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Stimm-/Wahlbezirk        | Ausprägung der bestehenden Entität `Wahlbezirk`                                            | Organisatorische Einheit der Urnenwahl                                                                                                                                     |
| Briefwahlbezirk          | Ausprägung der bestehenden Entität `Wahlbezirk`                                            | Organisatorische Einheit der Briefwahl. Die Stimmen werden durch einen Briefwahlvorstand ermittelt                                                                         |
| Stimmzettel              | Aus `Wahl`, `Stimmzettelgebiet` und den zugeordneten `Vorschlägen oder Vorlagen` ableitbar | Eine eigenständige Entität ist erst erforderlich, wenn beispielsweise Layout, Versionierung, Druckfreigabe oder Bereitstellung als Datei fachlich verwaltet werden sollen. |
| Partei oder Wählergruppe | Mögliche Eigenschaft oder ergänzende Entität zum `Wahlvorschlag`                           | Der Wahlvorschlag kann von einer Partei, einer Wählergruppe oder – abhängig von der Wahlart – von einer Einzelperson getragen werden.                                      |

### Zentraler Begriff: Stimmzettelgebiet

__Das __`Stimmzettelgebiet`__ ist ein wahlartenübergreifender Begriff des fachlichen Datenmodells.__ Es bezeichnet das Gebiet, innerhalb dessen bei einer bestimmten Wahl oder Abstimmung Stimmzettel mit demselben Inhalt verwendet werden.

Die konkrete Bedeutung des Stimmzettelgebiets hängt von der Wahlart ab. Bei einer Bundestagswahl entspricht es beispielsweise dem Wahlkreis, bei einer Landtags- oder Bezirkswahl dem Stimmkreis. Bei anderen Wahlen kann das gesamte Stadtgebiet oder ein räumlich abgegrenzter Teil des Stadtgebiets ein Stimmzettelgebiet bilden.

__Ein Wahlbezirk wird für jede innerhalb eines Wahltermins stattfindende Wahl dem jeweils maßgeblichen Stimmzettelgebiet zugeordnet.__ Dadurch wird festgelegt, welcher Stimmzettelinhalt für die betreffende Wahl in diesem Stimmbezirk oder Briefwahlbezirk zu verwenden ist.

[Quellen und mehr Informationen](https://stadt.muenchen.de/rathaus/politik/wahlen.html)

## Zuordnung der Wahl- und Abstimmungsarten im Überblick

Die folgenden Wahl- und Abstimmungsarten sind abzubilden. Wahlarten, die im Datenmodell nach demselben Grundmuster behandelt werden können, bleiben dennoch jeweils eigenständige Wahlen innerhalb eines Wahltermins.

| Wahl- oder Abstimmungsart | Stimmzettelgebiet                                                   | Abbildung im Datenmodell                                          |
|---------------------------|---------------------------------------------------------------------|-------------------------------------------------------------------|
| Europawahl                | für München ein Stimmzettelgebiet                                   | Wahlvorschläge mit Listenkandidaten                               |
| Bundestagswahl            | Wahlkreis                                                           | Wahlvorschläge mit Direktkandidaten und Landeslisten              |
| Landtagswahl              | Stimmkreis                                                          | Wahlvorschläge mit Direkt- und Listenkandidaten                   |
| Bezirkswahl               | Stimmkreis; strukturell wie Landtagswahl                            |                                                                   |
| Oberbürgermeisterwahl     | gesamtes Stadtgebiet                                                | Wahlvorschläge mit jeweils einer Direktkandidatur (Wahlvorschlag) |
| Stadtratswahl             | gesamtes Stadtgebiet                                                | Wahlvorschläge mit Listenkandidaten                               |
| Migrationsbeiratswahl     | gesamtes Stadtgebiet; strukturell wie Stadtratswahl                 | Wahlvorschläge mit Listenkandidaten                               |
| Bezirksausschusswahl      | je Stadtbezirk ein Stimmzettelgebiet; strukturell wie Stadtratswahl | Wahlvorschläge mit Listenkandidaten                               |
| Volksentscheid            | Freistaat Bayern                                                    | Referendumsvorlagen mit Referendumoptionen                        |
| Bürgerentscheid           | Gemeindegebiet, in München regelmäßig gesamtes Stadtgebiet          | Referendumsvorlagen mit Referendumoptionen                        |

__Hinweis:__ „Identisch“ bedeutet in dieser Übersicht nicht, dass die Wahlen rechtlich gleich sind. Gemeint ist, dass sie im fachlichen Datenmodell nach demselben Strukturmuster abgebildet werden können.

## Erläuterung zu einzelnen Wahl- und Abstimmungsarten

### Europawahl

__`Stimmzettelgebiet`__ – Für die Organisation der Europawahl in München genügt ein Stimmzettelgebiet, da in sämtlichen Münchner Stimmbezirken und Briefwahlbezirken derselbe Stimmzettel verwendet wird.
Bei der Europawahl können Parteien und sonstige politische Vereinigungen gemeinsame Listen für alle Länder oder Listen für einzelne Länder einreichen. Daher ist der Stimmzettel nicht zwingend bundesweit identisch. Für die Zuordnung innerhalb Münchens ist jedoch nur ein einheitliches Stimmzettelgebiet erforderlich.

__Abbildung im Modell:__ Die Europawahl wird als `Wahl` geführt. Die auf dem Stimmzettel enthaltenen Listen werden als `Wahlvorschläge` und die Bewerberinnen und Bewerber als `Listenkandidaten` abgebildet.

[Mehr Informationen zur Europawahl](https://stadt.muenchen.de/infos/europawahlen.html)

### Bundestagswahl

__`Stimmzettelgebiet`__ – Bei der Bundestagswahl entspricht das Stimmzettelgebiet dem Wahlkreis. Der Stimmzettel unterscheidet sich insbesondere durch die im Wahlkreis antretenden Direktkandidatinnen und Direktkandidaten.

Bei der Bundestagswahl 2025 umfasste die Landeshauptstadt München vier Wahlkreise:
- 216 - München-Nord
- 217 - München-Ost
- 218 - München-Süd
- 219 - München-West/Mitte

Jeder Münchner Stimmbezirk und Briefwahlbezirk wird für die Bundestagswahl genau einem dieser Wahlkreise zugeordnet. Die geografische Zugehörigkeit zu einem der 25 Stadtbezirke ist dafür nicht maßgeblich.

__Abbildung im Modell:__ Die Wahlkreisvorschläge werden als `Wahlvorschläge` mit `Direktkandidaten` abgebildet. Daneben enthält der Stimmzettel die Landeslisten.

![Wahlkreise in der Landeshauptstadt München](/fachlichesDatenmodell/Wahlkreiskarte_BTW2025.png)
*__Abbildung 2:__ Wahlkreise zur Bundestagswahl in München (Beispiel Bundestagswahl 2025).*

[Mehr Informationen zur Bundestagswahl](https://stadt.muenchen.de/infos/bundestagswahlen.html)

#### Landtagswahl und Bezirkswahl

__`Stimmzettelgebiet`__ – Bei der Landtagswahl entspricht das Stimmzettelgebiet dem Stimmkreis. Für die Bezirkswahl wird dieselbe räumliche Einteilung verwendet.

Landtagswahl und Bezirkswahl sind zwei eigenständige Wahlen. Sie werden im Datenmodell jeweils als eigene Wahl geführt und besitzen eigene Stimmzettelinhalte. Für beide Wahlen kann jedoch dieselbe räumliche Einteilung in Stimmkreise verwendet werden.

Das nachfolgende Beispiel zeigt die neun Münchner Stimmkreise der Landtagswahl 2023 (101 bis 109). Innerhalb eines Stimmkreises wird für die jeweilige Wahl dasselbe Stimmzettelpaar verwendet. Die Zugehörigkeit zu einem Stadtbezirk ist hierfür nicht maßgeblich.

__Abbildung im Modell:__ `Wahlvorschläge` enthalten `Direkt-` und `Listenkandidaten`. Landtagswahl und Bezirkswahl werden als getrennte `Wahlen` desselben `Wahltermins` geführt.

![Stimmkreiskarte in der Landeshauptstadt München](/fachlichesDatenmodell/Stimmkreiskarte_LTW_V1.jpg)
*__Abbildung 3:__ Stimmkreise zur Landtagswahl in München (Beispiel Landtagswahl 2023).*

Für jede Wahl gibt es 2 Stimmzettel (klein – mit Direktkandidatur; groß – mit Listenkandidaturen). Jeder Wahlvorschlag hat Listenkandidaturen, aber nicht immer eine Direktkandidatur.

[Mehr Informationen zur Landtagswahl.](https://stadt.muenchen.de/infos/landtagswahlen-und-bezirkswahlen-teil-ii.html)

#### Oberbürgermeisterwahl

__`Stimmzettelgebiet`__ – Bei der Oberbürgermeisterwahl ist das gesamte Gebiet der Landeshauptstadt München ein Stimmzettelgebiet. In allen Münchner Stimmbezirken und Briefwahlbezirken wird derselbe Stimmzettel verwendet.

__Abbildung im Modell:__ Jeder zugelassene `Wahlvorschlag` umfasst eine unmittelbar wählbare Bewerberin oder einen unmittelbar wählbaren Bewerber. Diese Person wird als `Direktkandidat` abgebildet.

Eine erforderliche Stichwahl findet an einem weiteren Datum statt und wird daher als eigener `Wahltermin` mit einer eigenen `Wahl` geführt.

### Stadtratswahl

__`Stimmzettelgebiet`__ – Bei der Stadtratswahl ist das gesamte Gebiet der Landeshauptstadt München ein Stimmzettelgebiet. Der Stimmzettel ist in allen Münchner Stimmbezirken und Briefwahlbezirken identisch.

__Abbildung im Modell:__ Die zur Wahl stehenden Listen werden als `Wahlvorschläge` und die darauf geführten Bewerberinnen und Bewerber als `Listenkandidaten` abgebildet. Die besonderen Regeln zur Stimmenvergabe, beispielsweise Kumulieren, Panaschieren oder Streichen, betreffen die Auswertung des Stimmzettels, ändern aber nicht die grundlegende Struktur des Datenmodells.

#### Migrationsbeiratswahl

__`Stimmzettelgebiet`__ – Bei der Migrationsbeiratswahl ist das gesamte Gebiet der Landeshauptstadt München ein Stimmzettelgebiet.

__Abbildung im Modell:__ Die Migrationsbeiratswahl wird strukturell wie die Stadtratswahl abgebildet: `Wahlvorschläge` enthalten `Listenkandidaten`. Sie bleibt jedoch eine eigenständige `Wahl`. Da sich der Kreis der Wahlberechtigten von anderen Wahlen unterscheidet, kann sie in einem gesonderten `Wahltermin` mit eigenen `Wahlbezirken` organisiert werden.

### Bezirksausschusswahl

__`Stimmzettelgebiet`__ – Bei der Bezirksausschusswahl entspricht das Stimmzettelgebiet dem jeweiligen Stadtbezirk. München besitzt 25 Stadtbezirke und damit für die Bezirksausschusswahl 25 unterschiedliche Stimmzettelgebiete.

__Abbildung im Modell:__ Die Bezirksausschusswahl wird strukturell wie die Stadtratswahl abgebildet: `Wahlvorschläge` enthalten `Listenkandidaten`. Anders als bei der Stadtratswahl unterscheiden sich die Stimmzettelinhalte jedoch nach dem Stadtbezirk.

Bei einer gleichzeitig stattfindenden Kommunalwahl ist ein `Wahlbezirk` daher für die Oberbürgermeisterwahl und die Stadtratswahl jeweils einem stadtweiten `Stimmzettelgebiet`, für die Bezirksausschusswahl aber dem `Stimmzettelgebiet` seines Stadtbezirks zugeordnet.

### Volksentscheid

__`Stimmzettelgebiet`__ – Bei einem bayerischen Volksentscheid ist der Freistaat Bayern das Stimmzettelgebiet. Für die Durchführung in München wird daher ein einheitlicher Stimmzettelinhalt verwendet.

__Abbildung im Modell:__ Die zur Abstimmung gestellte Frage oder Vorlage wird als `ReferendumVorlage` und die auswählbaren Antworten werden als `ReferendumOptionen` abgebildet. Finden mehrere Abstimmungen gleichzeitig statt oder ist eine Stichfrage erforderlich, werden mehrere `Vorlagen` geführt.

### Bürgerentscheid

__`Stimmzettelgebiet`__ – Bei einem Bürgerentscheid ist grundsätzlich das Gebiet der Gemeinde das Stimmzettelgebiet. Für einen Bürgerentscheid der Landeshauptstadt München ist dies regelmäßig das gesamte Stadtgebiet.

__Abbildung im Modell:__ Die Fragestellung wird als `ReferendumVorlage` und die Antwortmöglichkeiten werden als `ReferendumOptionen` abgebildet. Bei mehreren Bürgerentscheiden oder einer Stichfrage werden mehrere `Vorlagen` geführt.

Ein Bürgerentscheid kann gemeinsam mit einer Wahl durchgeführt werden. Wenn die organisatorische Durchführung gemeinsam erfolgt, kann er demselben `Wahltermin` zugeordnet werden.

## Beispiele für die Zuordnung bei gleichzeitig stattfindenden Wahlen

Die besondere Stärke des Modells liegt darin, dass ein Wahlbezirk innerhalb eines Wahltermins für unterschiedliche Wahlen unterschiedlichen Stimmzettelgebieten zugeordnet werden kann. Die folgenden Beispiele verdeutlichen dies.

| Beispiel                                           | Zuordnung im fachlichen Datenmodell                                                                                                                                                                                                                                                                    |
|----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Kommunalwahl in München                            | Ein Stimmbezirk gehört bei der Oberbürgermeisterwahl zum stadtweiten `Stimmzettelgebiet` *__München__*, bei der Stadtratswahl ebenfalls zum stadtweiten `Stimmzettelgebiet` *__München__* und bei der Bezirksausschusswahl zum `Stimmzettelgebiet` des jeweiligen Stadtbezirks.                        |
| Bundestagswahl und Bürgerentscheid am selben Datum | Ein Stimmbezirk kann bei der Bundestagswahl dem maßgeblichen Bundestagswahlkreis und beim Bürgerentscheid zugleich dem stadtweiten `Stimmzettelgebiet` *__München__* zugeordnet werden. Werden beide Abstimmungen organisatorisch gemeinsam durchgeführt, können sie demselben `Wahltermin` angehören. |
| Landtagswahl und Bezirkswahl                       | Beide Wahlen finden regelmäßig gemeinsam statt und verwenden dieselbe räumliche Einteilung in Stimmkreise. Im Modell werden dennoch zwei `Wahlen` mit jeweils eigenen `Stimmzettelgebieten`, `Wahlvorschlägen` und Stimmzettelinhalten geführt.                                                        |
| Migrationsbeiratswahl                              | Aufgrund des abweichenden Kreises der Wahlberechtigten kann die Migrationsbeiratswahl in einem gesonderten `Wahltermin` mit eigenen `Wahlbezirken` organisiert werden. Strukturell bleibt sie wie die Stadtratswahl abbildbar.                                                                         |
