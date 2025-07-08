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

📃 **UseCase: Initiales Laden der Daten**

In einem ersten Schritt nach dem Login werden die hinterlegten Daten des angemeldeten Benutzers geladen. Kommt es hier
zu einem Fehler, wird der Wahlvorstand dazu aufgefordert, sich beim Wahlamt zu melden. Ist der Schritt erfolgreich,
müssen anschließend alle für die Wahlen wichtigen Daten ebenfalls erfolgreich geladen werden. Bei Fehlern wird dem
Nutzer der Zugriff auf die Anwendung verwehrt. Das erfolgreiche Laden ermöglicht später auch eine reibungslose Nutzung
im Offline-Modus.

Daten, die unabhängig von der Wahlbezirksart für eine **Kommunalwahl** geladen werden:
*Wahlvorstand*, *Wahlvorschläge*, *Kopfdaten*, *Konfigurationen*, *Handbuch*, *Wahlvorbereitung*, *Eröffnungsuhrzeit*,
*Störungen*, *Druckstatus*, *Stapel c - gültige Stimmzettel*, *Stapel c - ungültige Stimmzettel*, *Stapel b -
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

Zu jeder Zeit der Wahlhandlung kann der Nutzer Ereignisse in der Anwendung erfassen. Sind keine Ereignisse vorgefallen,
muss dies explizit durch das Aktivieren von Checkboxen bestätigt werden, um die Maske speichern zu können. Sind jedoch
Ereignisse eingetreten, müssen diese einzeln erfasst werden. Jedes Ereignis hat

- eine Ereignisart
- ein Datum und eine Uhrzeit
- eine aussagekräftige Beschreibung mit mindestens 4 und maximal 500 Zeichen.

Bei Bedarf können eingetragene Ereignisse auch wieder gelöscht werden. Das Löschen aller eingetragenen Ereignisse
aktiviert automatisch die "Keine Ereignisse vorgefallen"-Checkboxen. Das Eintragen neuer Ereignisse, wenn vorher noch
keine gespeichert wurden, deaktiviert die Checkboxen. Diese Relation wird für jeden Wahlbezirk in den Attributen
`keineVorfaelle` und `keineVorkommnisse` gespeichert, die sich daraus berechnen, ob es Einträge mit der entsprechenden
Ereignisart gibt.

::: info Ereignisse im Urnenwahlbezirk {data-uwb="true"}
Im Urnenwahlbezirk wird zwischen Vorfällen, die am Wahltag während der Stimmabgabe auftreten, und Vorkommnissen, die
während der Auszählung auftreten, unterschieden. Vorkommnisse können also auch an einem Tag nach der Wahl auftreten und
erfasst werden. Wird die Uhrzeit eines Vorkommnisses nachträglich auf vor der Wahlschließung gesetzt, passt sich auch
die Ereignisart des Eintrags entsprechend an und wird zu einem Vorfall, und andersherum. Dementsprechend ist die
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
Für alle Mitglieder, die tatsächlich anwesend sind, muss die Anwesenheit manuell per Checkbox erfasst werden. Die
Anwesenheit kann nur bei Erfüllung folgender Bedingungen gespeichert werden:

- es muss mindestens ein Wahlvorsteher anwesend sein
- es muss mindestens ein Schriftführer anwesend sein
- vor der Wahlschliessung müssen mindestens 3 Mitglieder anwesend sein
- nach der Wahlschliessung müssen mindestens 5 Mitglieder anwesend sein

Der Nutzer der Anwendung wird durch eine Fehlermeldung darauf hingewiesen, wenn diese Bedingungen nicht erfüllt sind.

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

📃 **UseCase: `Wahl eröffnen`**

Der Nutzer wird dazu aufgefordert, die Uhrzeit einzutragen, zu welcher die Stimmabgabe begonnen wurde, beziehungsweise
zu welcher der Wahlvorstand zusammengetreten ist. Um speichern zu können, darf der Nutzer keine Zeit eingeben, die in
der Zukunft liegt. Ausserdem müssen die folgenden Grenzwerte je nach Wahlbezirksart berücksichtigt werden:

::: info `Erfassung der Öffnung des Wahllokals` im Urnenwahlbezirk {data-uwb="true"}
Die früheste Zeit, zu der das Wahllokal geöffnet werden kann, ist der Standardwert von 8 Uhr und wird mit dem
Konfigurationsparameter `FRUEHESTE_EROEFFNUNGSUHRZEIT_UW` geladen. Die späteste Zeit zur Öffnung richtet sich nach dem
Konfigurationsparameter `FRUEHESTE_SCHLIESSUNGSUHRZEIT_UW` mit einem Standardwert von 18 Uhr.
:::

::: info `Erfassung des Zusammentretens des Wahlvorstands` im Briefwahlbezirk {data-bwb="true"}
Die früheste Zeit, zu welcher der Wahlvorstand zusammentreten kann, ist der Standardwert von 15 Uhr und wird mit dem
Konfigurationsparameter `FRUEHESTE_EROEFFNUNGSUHRZEIT_BW` geladen.
:::

##### Während der Stimmabgabe

📃 **UseCase: `Erfassung der absoluten Wahlbeteiligung`**

::: info Urnenwahlbezirk {data-uwb="true"}
In einem Urnenwahlbezirk kann nach der Öffnung der Wahl die Anzahl der Wähler, die das Wahllokal besuchen, erfasst
werden. Jedes Mal, wenn der Nutzer auf den entsprechenden Button klickt, oder alternativ eine der Tasten `Eingabe`, `+`
oder `Leertaste` drückt, wird die Wähleranzahl um 1 erhöht.
:::

📃 **UseCase: Wahl schliessen**

::: info `Erfassung der Schliessung des Wahllokals` im Urnenwahlbezirk {data-uwb="true"}
Analog zur Öffnung der Wahl, wird sie vor der Auszählung auch wieder geschlossen. Die früheste Zeit zur Schließung
richtet sich nach dem Konfigurationsparameter `FRUEHESTE_SCHLIESSUNGSUHRZEIT_UW` mit einem Standardwert von 18 Uhr.
Auch diese darf nicht in der Zukunft liegen.
:::
