# About

In diesem Repository wollen wir das Wahllokalsystem der Landeshauptstadt München, das initial zur Bundestagswahl 2017 eingeführt wurde,
veröffentlichen. Anlass dafür ist eine geplante Lifecyclemaßnahme. Da wir im Rahmen dieser Maßnahme die gesamte Anwendung betrachten,
bot es sich an entsprechend der [OpenSource-Strategie](https://opensource.muenchen.de/de/principles.html#rechtliche-und-politische-vorgaben)
der Landeshauptstadt München, die Anwendung OpenSource zu stellen.

Am Ende soll die Anwendung für Interessierte vollständig zur Verfügung stehen.

## Was kann die Software

Der Hauptfokus der Software liegt auf den Handlungen am Wahltag. Sie unterstützt bei der Eröffnung des Wahllokals, indem man z.B. anhand einer Checkliste
prüfen kann, dass alle notwendigen Materialien für die Durchführung vorhanden sind. Während der Zeit der Stimmabgabe wird übermittelt,
wie viele Wähler\*innen bereits im Wahllokal waren. Des Weiteren werden über die Software wahlrelevante Vorkommnisse dokumentiert.

Außerdem wird zu definierten Zeitpunkten abgefragt, welche Mitglieder des Wahlvorstandes im Wahllokal anwesend sind. So wird sichergestellt,
dass das Gremium zu jeder Zeit ausreichend besetzt ist.

Während der Stimmauszählung, und der damit verbundenen Erstellung der Niederschrift, unterstützt das System den Wahlvorstand bei der korrekten Erfassung der Daten.
Dazu gibt es zahlreiche Regeln und Konsistenzprüfungen, die dem Wahlvorstand Feedback geben, ob die erfassten Daten korrekt sind.

Das Wahlamt kann über das System den Wahlbezirken wichtige Informationen zukommen lassen.

![Übersicht der Funktionen des Wahllokalsystems im zeitlichen Kontext einer Wahl](/relationshipOfUIsToElectionProcess.drawio.png)  
*Übersicht der Funktionen des Wahllokalsystems im zeitlichen Kontext einer Wahl*

## Fachliche Anforderungen

Im Folgenden werden die fachlichen Anforderungen an die Software je nach Funktion näher beschrieben. Die Einordnung in
die Grafik lässt sich der jeweiligen Überschrift "📃 **UseCase: `Titel`**" entnehmen.

### Wahllokalsystem UI

Das Wahllokalsystem ist die Anwendung, die am Wahltag vom Wahlvorstand bedient wird.

📃 **UseCase: `Initiales Laden der Daten`**

In einem ersten Schritt nach dem Login werden die hinterlegten Daten des angemeldeten Benutzers geladen. Kommt es hier
zu einem Fehler, wird der Wahlvorstand dazu aufgefordert, sich beim Wahlamt zu melden. Ist der Schritt erfolgreich,
müssen anschließend alle für die Wahlen wichtigen Daten ebenfalls erfolgreich geladen werden. Schlägt das Laden fehl,
kann die Initialisierungs-Anfrage für die betroffenen Daten wiederholt werden. Bei bestimmten fehlenden Daten wird dem
Nutzer der Zugriff auf die Anwendung verwehrt, bei anderen, mit niedrigerer Priorität, ist der Zugriff auf die Anwendung
trotzdem möglich. Das erfolgreiche Laden ermöglicht später auch eine reibungslose Nutzung im Offline-Modus.

Folgende Daten werden bei einer **Kommunalwahl** von beiden Wahlbezirksarten "Urnenwahlbezirk" und "Briefwahlbezirk"
geladen:
*Wahlvorstand*, *Wahlvorschläge*, *Kopfdaten*, *Konfigurationen*, *Handbuch*, *Wahlvorbereitung*, *Eröffnungsuhrzeit*,
*Ereignisse*, *Druckstatus*, *Stapel c - gültige Stimmzettel*, *Stapel c - ungültige Stimmzettel*, *Stapel b -
ungekennzeichnete Stimmzettel*, *Begründung - Stapel a*, *Stapel d*, *Stapel a*, *Stapel b*, *Begründung Stapel ab*,
*Stapel b-c*, *FortsetzungsUhrzeit*, *UnterbrechungsUhrzeit*

::: info Kommunalwahl-Daten, die nur in einem Urnenwahlbezirk geladen werden {data-uwb="true"}
*UngültigeWahlscheine*, *A-Werte*, *Wählerverzeichnis*, *Schließungsuhrzeit*, *Stimmabgabevermerke*, *Stimmzettel*,
*Begründung - Stimmzettel*
:::

::: info Kommunalwahl-Daten, die nur in einem Briefwahlbezirk geladen werden {data-bwb="true"}
*Erfasste Wahlbriefe*, *Zugelassene Wahlbriefe*, *Wahlscheine*, *Stimmzettelumschläge*, *Begründung -
Stimmzettelumschläge*, *Stapel b - leere Stimmzettelumschläge*
:::

📃 **UseCase: `Erfassung von Ereignissen`**

Zu jeder Zeit der Wahlhandlung kann der Nutzer Ereignisse in der Anwendung erfassen. Jedes Ereignis hat folgende
Pflichtfelder:

- eine (berechnete) Ereignisart (siehe Infobox ["Ereignisse im Urnenwahlbezirk"](./#infobox-ereignisse-uwb))
- ein Datum mit Uhrzeit
- eine aussagekräftige Beschreibung mit mindestens 4 und maximal 500 Zeichen.

Weiterhin gibt es einige Verhaltensregeln für die Ereigniserfassung:

- Wenn keine Ereignisse vorgefallen sind, muss dies explizit durch das Aktivieren einer Checkbox bestätigt werden, um
  die Maske speichern zu können.
- Sind Ereignisse eingetreten, müssen diese einzeln erfasst werden.
- Bei Bedarf können eingetragene Ereignisse wieder gelöscht werden.
- Das Löschen aller eingetragenen Ereignisse aktiviert automatisch die Checkboxen "Keine Ereignisse vorgefallen".
- Das Eintragen neuer Ereignisse, wenn vorher noch keine gespeichert wurden, deaktiviert die Checkboxen automatisch.

Diese Relationen werden für jeden Wahlbezirk in den Attributen `keineVorfaelle` und `keineVorkommnisse` gespeichert,
die sich daraus berechnen, ob es Einträge mit der entsprechenden Ereignisart gibt.

{#infobox-ereignisse-uwb}
::: info Ereignisse im Urnenwahlbezirk {data-uwb="true"}
Im Urnenwahlbezirk wird zwischen Vorfällen, die am Wahltag während oder vor der Stimmabgabe auftreten, und Vorkommnissen, die
während der Auszählung auftreten, unterschieden. Vorkommnisse können also auch an einem Tag nach der Wahl auftreten und
erfasst werden. Wird die Uhrzeit eines Vorkommnisses nachträglich auf vor der Wahlschließung gesetzt, passt sich auch
die Ereignisart des Eintrags entsprechend an und wird zu einem Vorfall, und andersherum. Sobald die Schliessungsuhrzeit
gespeichert wird, wird die Ereignisart der Ereignisse ebenfalls neu berechnet. Dementsprechend ist die
Ereignisart eng verknüpft mit dem zugehörigen Datum und der Uhrzeit.
:::

::: info Ereignisse im Briefwahlbezirk {data-bwb="true"}
Im Briefwahlbezirk werden nur Vorkommnisse erfasst. Dies ist erst möglich, sobald für mindestens eine Wahl eine Wahlurne
geöffnet wurde.
:::

#### Vor der Auszählung

##### Vor der Stimmabgabe

📃 **UseCase: `Erfassung der Anwesenheit des Wahlvorstands`**

Beim Start der Anwendung wurden alle Wahlvorstandsmitglieder geladen, die für diesen Tag im Wahlbezirk eingeteilt sind.
Die Liste der Wahlvorstandsmitglieder ist nach Funktion in folgender Reihenfolge sortiert:

1. Wahlvorsteher*in
2. Stellvertretung Wahlvorsteher*in
3. Schriftführer*in
4. Stellvertretung Schriftführer*in
5. Beisitzer*in

Bei gleicher Funktion wird zusätzlich nach Familienname und Vorname sortiert.

Für alle Mitglieder, die tatsächlich anwesend sind, muss die Anwesenheit manuell per Checkbox erfasst werden. Die
Anwesenheit kann nur bei Erfüllung folgender Bedingungen gespeichert werden:

- es muss mindestens ein Wahlvorsteher oder dessen Stellvertreter anwesend sein
- es muss mindestens ein Schriftführer oder dessen Stellvertreter  anwesend sein
- vor der Wahlschließung müssen mindestens 3 Mitglieder anwesend sein
- nach der Wahlschließung müssen mindestens 5 Mitglieder anwesend sein

Der Nutzer der Anwendung wird durch eine Fehlermeldung darauf hingewiesen, wenn diese Bedingungen nicht erfüllt sind.
Über einen "Aktualisieren"-Button, welcher den Wahlvorstand mit einem `forceUpdate`-Flag neu lädt, kann der Nutzer
erzwingen, die Mitglieder durch eine mögliche neue Zusammensetzung zu überschreiben.

::: info 📃 UseCase: `Druck einer Wahlvorstand-Nachbesetzung` im Briefwahlbezirk {data-bwb="true"}
In manchen Fällen ist es in einem Briefwahlbezirk notwendig, eine Nachbesetzung zu organisieren. Hierzu kann der Nutzer
über den Button `Nachbesetzung drucken` ein entsprechendes PDF-Dokument generieren lassen, welches im Anschluss gedruckt
und ausgefüllt werden kann.
:::

📃 **UseCase: `Vorbereitung der Wahlhandlung - Prüfung der Ausstattung des Wahllokals`**

Für jede an dem entsprechenden Tag stattfindende Wahl muss die Anzahl der zur Verfügung gestellten Wahlurnen erfasst
werden. Der Nutzer muss bestätigen, dass die Wahlurnen vor der Nutzung leer waren und versiegelt wurden, um speichern zu
können.

::: info Abstimmungsschutzvorrichtungen im Urnenwahlbezirk {data-uwb="true"}
In jedem Urnenwahllokal müssen gewisse Abstimmungsschutzvorrichtungen gewährleistet sein, um das Wahlgeheimnis nicht zu
gefährden. Dementsprechend muss die Summe der Anzahl der Tische mit Sichtblenden, der Nebenräume im Wahlraum, sowie der
Wahlkabinen insgesamt mindestens 1 ergeben, bevor der Nutzer speichern kann.
:::

📃 **UseCase: `Vorbereitung der Wahlhandlung - Auf ungültige Wahlscheine hinweisen`**

::: info Pflege des Wählerverzeichnisses im Urnenwahlbezirk {data-uwb="true"}
Vor der Stimmabgabe muss sichergestellt werden, dass das Wählerverzeichnis aktuell und korrekt ist. Dazu müssen
nachträglich erteilte Wahlscheine berücksichtigt werden, um die Integrität der Wahl zu gewährleisten und allen  
wahlberechtigten Personen die Möglichkeit zu bieten, ihre Stimmen abzugeben. Es muss immer mindestens die Option
ausgewählt werden, mit welcher bestätigt wird, dass der Wahlvorstand über ungültige Wahlscheine unterrichtet wurde. Der
Wahlvorstand wird vom Wahlamt darüber benachrichtigt, ob weitere Korrekturen am Wählerverzeichnis und der
zugehörigen Ansicht in der Anwendung vorzunehmen sind.
:::

📃 **UseCase: `Wahl eröffnen`**

Der Nutzer wird dazu aufgefordert, die Uhrzeit einzutragen, zu welcher die Stimmabgabe begonnen wurde, beziehungsweise
zu welcher der Wahlvorstand zusammengetreten ist. Um speichern zu können, darf der Nutzer keine Zeit eingeben, die in
der Zukunft liegt. Außerdem müssen die folgenden Grenzwerte je nach Wahlbezirksart berücksichtigt werden:

::: info `Erfassung der Öffnung des Wahllokals` im Urnenwahlbezirk {data-uwb="true"}
Die früheste Zeit, zu der das Wahllokal geöffnet werden kann, ist der Standardwert von 8 Uhr und wird mit dem
Konfigurationsparameter `FRUEHESTE_EROEFFNUNGSUHRZEIT_UW` geladen. Die späteste Zeit zur Öffnung richtet sich nach dem
Konfigurationsparameter `FRUEHESTE_SCHLIESSUNGSUHRZEIT_UW` mit einem Standardwert von 18 Uhr.
:::

::: info `Erfassung des Zusammentretens des Wahlvorstands` im Briefwahlbezirk {data-bwb="true"}
Die früheste Zeit, zu welcher der Wahlvorstand zusammentreten kann, ist der Standardwert von 15 Uhr und wird mit dem
Konfigurationsparameter `FRUEHESTE_EROEFFNUNGSUHRZEIT_BW` geladen.
:::

📃 **UseCase: `Erfassung der erhaltenen Wahlbriefe`**

::: info Briefwahlbezirk {data-bwb="true"}
In einem Briefwahlbezirk werden die erhaltenen Wahlbriefe erfasst. Die dabei relevanten Werte sind die Anzahl der
Wahlbriefe, die vor 18 Uhr übergeben wurden. Dabei muss mindestens ein Wahlbrief erfasst werden. Ebenso gilt es die
Anzahl der Verzeichnisse der für ungültig erklärten Wahlscheine sowie die Anzahl der Nachträge zu den Verzeichnissen zu
erfassen. Erst nach der Angabe dieser drei Werte können die Informationen gespeichert werden. Optional ist die Angabe
einer Anzahl an nachgelieferten Wahlbriefen nach 18 Uhr. Sollte es nachgelieferte Wahlbriefe geben, so muss eine Uhrzeit
dazu angegeben werden.
:::

##### Während der Stimmabgabe

📃 **UseCase: `Erfassung der absoluten Wahlbeteiligung`**

::: info Urnenwahlbezirk {data-uwb="true"}
In einem Urnenwahlbezirk kann nach der Öffnung der Wahl die Anzahl der Wähler, die das Wahllokal besuchen, erfasst
werden. Jedes Mal, wenn der Nutzer auf den entsprechenden Button klickt, wird die Wähleranzahl um 1 erhöht. Alternativ
kann der Nutzer das Feld über seine Tastatur steuern. Wenn er sich nicht in einem Input-Feld befindet, kann er die Taste
`+` drücken, oder, wenn der Zählbutton fokussiert ist, die Tasten `Eingabe` oder `Leertaste`.
:::

📃 **UseCase: `Wahl schliessen`**

::: info `Erfassung der Schließung des Wahllokals` im Urnenwahlbezirk {data-uwb="true"}
Analog zur Öffnung der Wahl, wird sie vor der Auszählung auch wieder geschlossen. Die früheste Zeit zur Schließung
richtet sich nach dem Konfigurationsparameter `FRUEHESTE_SCHLIESSUNGSUHRZEIT_UW` mit einem Standardwert von 18 Uhr.
Auch diese darf nicht in der Zukunft liegen.
:::

::: info Briefwahlbezirk {data-bwb="true"}
Im Briefwahlbezirk gibt es keine "Schließung" des Wahllokals. Es wird aber eine Uhrzeit mit dem UseCase `Erfassung
abgegebener Stimmen` übermittelt, welche der Schliessungsuhrzeit im Urnenwahllokal gleicht. Die früheste Zeit, die
übermittelt werden kann richtet sich nach dem Konfigurationsparameter `FRUEHESTE_SCHLIESSUNGSUHRZEIT_BW` mit einem
Standardwert von 18 Uhr.
:::

📃 **UseCase: `Überprüfung der ungültigen Wahlscheine`**

::: info Urnenwahlbezirk {data-uwb="true"}
In einem Urnenwahlbezirk muss, wenn jemand mit Wahlschein kommt, geprüft werden, ob dieser Wahlschein gültig ist. Dazu
gibt es in der Anwendung eine Liste ungültiger Wahlscheine. Der Nutzer gibt die Nummer des Wahlscheins ein und sucht.
Nach der Suche erhält der Nutzer Feedback darüber, ob der Wahlschein gültig oder ungültig ist. Entsprechend werden auch
Handlungsanweisungen angezeigt. Ist der Wahlschein ungültig, werden in der Fehlermeldung neben der Wahlscheinnummer
auch Vor- und Familienname angezeigt. Außerdem wird der Nutzer in diesem Fall dazu aufgefordert, den Beschluss über die
Zurückweisung als Ereignis zu erfassen.

Um suchen zu können, muss eine Wahlscheinnummer vorhanden sein. Die Wahlscheinnummer muss außerdem im Bereich von `1` bis
`9999999` liegen.

Nach einer Suche kann über den Button, der zuvor die Suche ausgeführt hat, die Suche zurückgesetzt werden. Dabei
werden die Eingabe und Handlungsanweisungen entfernt. Die Suche wird ebenfalls zurückgesetzt, wenn der Nutzer
die Wahlscheinnummer verändert.

Zur Unterstützung des Wahlvorstands wird ein Bild angezeigt, das helfen soll, die Stelle, wo die Wahlnummer steht,
zu identifizieren.

Die vorhandene Liste kann über den Aktualisieren-Button erneut geladen werden.

Ist die Liste leer oder konnte die Liste nicht geladen werden, erhält der Nutzer entsprechende Fehlermeldungen
angezeigt.
:::

📃 **UseCase: `Beschluss zu uneindeutigen Wahlbriefen erfassen`**

::: info Briefwahlbezirk {data-bwb="true"}
Der Wahlvorstand kann über jeden bedenklichen Wahlbrief einen individuellen Beschluss fassen. Dabei wird zwischen der
Gültigkeit des gesamten Wahlscheins und anschließend der einzelnen Stimmzettelumschläge unterschieden. Ein Wahlschein
kann die Werte `Zugelassen`, `Wahlschein ungültig laut Liste`, `Kein Original-Wahlschein`, oder `Unterschrift auf
Wahlschein fehlt` haben. Für die Stimmzettelumschläge gibt es die Zurückweisungsgründe `Zugelassen`,
`Stimmzettelumschlag fehlt`, `Lose Stimmzettel`, `Wahlbrief und Stimmzettelumschlag offen`, `Wahlscheine ungleich
Stimmzettelumschläge`, `Nicht-amtlicher Stimmzettelumschlag`, `Stimmzettelumschlag gefährdet Wahlgeheimnis`, `Gegenstand
im Stimmzettelumschlag` oder `Für diese Wahl nicht wahlberechtigt`.

Es muss immer zuerst der Beschluss für den gesamten Wahlschein gefasst werden. Ist der Wahlschein nicht `Zugelassen`,
bekommen die Stimmzettelumschläge der einzelnen Wahlen den gleichen Zurückweisungsgrund. Ist der Wahlschein
`Zugelassen`, wird der im Anschluss gewählte Stimmzettelumschlag-Zurückweisungsgrund auf die anderen Wahlen übertragen.
Der einzige Beschluss, der unabhängig für eine Wahl gefasst werden kann, ohne dass die anderen Wahlen den gleichen
Zurückweisungsgrund erhalten, ist `Für diese Wahl nicht Wahlberechtigt`.

Der Wahlvorstand kann die Daten der Eingabemaske nur speichern, wenn für alle Wahlscheine und Stimmzettelumschläge ein
valider Wert eingetragen wurde.
:::

##### Während der Auszählung

📃 **UseCase: `Erfassung abgegebener Stimmen`**

Im Urnenwahl- sowie im Briefwahlbezirk kann der Wahlvorstand die Anzahl der abgegebenen Stimmen erfassen. Die
Erfassung erfolgt für jede Wahl einzeln.

::: info `Zählen der Stimmzettel` im Urnenwahlbezirk {data-uwb="true"}
Der Wahlvorstand wird dazu aufgefordert, die Anzahl der erhaltenen Stimmzettel zu zählen und zu erfassen.
:::

::: info `Zählen der Stimmzettelumschläge` im Briefwahlbezirk {data-bwb="true"}
Der Wahlvorstand wird dazu aufgefordert, die Anzahl der erhaltenen Stimmzettelumschläge zu zählen und zu erfassen.
Außerdem muss eine Uhrzeit mit erfasst werden, zu welcher die Wahlurne geöffnet wurde. Diese Uhrzeit darf nicht in
der Zukunft liegen und muss größer oder gleich der frühesten Schließungsuhrzeit sein
(Konfiguration `FRUEHESTE_SCHLIESSUNGSUHRZEIT_BW`, Standardwert 18:00).
:::

📃 **UseCase: `Beschluss zu uneindeutigen Stimmzettel fassen`**

::: info `Oberbürgermeisterwahl`
Die Ergebnisse werden mit Stapel C erfasst. Dieser besteht aus den Teilstapeln für gültige und ungültige Stimmen.
Für jeden bedenklichen Stimmzettel muss eine Entscheidung getroffen werden. Bei den gültigen Stimmzetteln handelt es
sich um solche, bei denen die Entscheidung für einen konkreten Wahlvorschlag getroffen wurde.

Die Reihenfolge der getroffenen Beschlüsse muss stabil bleiben.
:::