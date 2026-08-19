# Naming Convention für Frontend Elemente

Bitte den [StyleGuide](https://vuejs.org/style-guide/) beachten.

- Dateinamen sind in [c|C]amelCase
- Definitionen von Properties, Events und lokalen Variablen in camelCase
- Element-Attribute in Kebab-Case

Die Benennung erfolgt grundsätzlich auf Englisch, mit Ausnahme von Fachbegriffen welche auf Deutsch verwendet werden.

## Views

`[UWB|BWB]<Domain>[<Funktion>]View.vue`

Der Prefix `UWB` oder `BWB` ist zu setzen wenn diese View nur für eine bestimmte Wahlbezirksart gedacht ist. Erfolgt
innerhalb der View eine Unterscheidung welche Subkomponenten angezeigt werden, oder ist die View unabhängig von der
Wahlbezirksart, ist kein Prefix zu vergeben.

### Beispiel

`WahlvorstandView.vue` ... eine View für den Wahlvorstand, die für alle Wahlbezirksarten gedacht ist  
`UWBWaehlerverzeichnisView.vue` ... eine View die nur bei der Wahlbezirksart `UWB` angezeigt werden soll

## Components

### Basiskomponenten

`Base<Type>[<Domain>]<Funktion>.vue`

**Beispiele:**

`BaseButtonRefresh.vue` ... Allgemeiner Button zur Aktualisierung

`BaseButtonSave.vue` ... Allgemeiner Button zum Speichern

`BaseWahlvorstandMitgliedCard.vue` ... V-Card zur Darstellung eines Wahlvorstandsmitgliedes

### Single-Instance-Komponenten

`The[<Domain>]<Funktion><Type>.vue`

> [!NOTE]
> Die Bezeichnung stammt aus einem älteren [StyleGuide](https://v2.vuejs.org/v2/style-guide/#Single-instance-component-names-strongly-recommended)

**Beispiele:**

`TheWahlvorstandLastSendDiv.vue` ... Wahlvorstandskomponente welche die Uhrzeit der letzten Übermittlung darstellt

`TheWahlvorstandMitgliederTable.vue` ... Wahlvorstandskomponente welche Mitglieder des Wahlvorstandes als Tabelle anzeigt

## Stores

`<domain>Store.ts`

**Beispiel:**

`wahlvorstandStore.ts` ... Store für den Wahlvorstand

## Composables

### Rules

`[<domain>]Rules.ts`

Für eine Modularisierung ist es möglich spezielle Rules einer Domain zuzuordnen.

### Formatter

`<domain|typ>Formatter.ts`

Formatter gehören zu einem Datentyp oder einer Domain.

**Beispiel:**

`textFormatter.ts` ... Formartierungsfunktionen für Text

`dateTimeFormatter.ts` ... Formatierungsfunktionen für Date

### Utils

`<komponentenName>Utils.ts`

**Beispiel:**

`theBeschlussfassungStartenDialogUtils.ts` ... ist das Utils-Composable für die Komponente `TheBeschlussfassungStartenDialog`

### StoreModule

`<domain>[<Funktion>]StoreModule.ts`

**Beispiel:**

`briefwahlGetterStoreModule.ts` ... kapselt Funktionalität zu einem Thema eines Stores

### Mapper

`<domain>Mapper.ts`

**Beispiel:**

`wahlvorstandMapper.ts` ... enthält Mappingfunktionen zum Wahlvorstand

### FetchService

`<domain>FetchService.ts`

**Beispiel:**

`wahlvorstandFetchService.ts` ... stellt Funktionen bereit, um Daten des Wahlvorstandes aus dem Backend abzurufen

### Tools

`<typ>Tools.ts`

**Beispiel:**

`wahlvorstandsMitgliedTools.ts` ... stellt Funktionen rund um den Datentyp bereit, wie z.B. die Erstellung eines fachlich leeren Objektes

### Manager

`<topic>Manager.ts`

**Beispiel:**

`requestStrategyManager.ts` ... kapselt die diversen RequestStrategien und entscheidet im Rahmen der Verarbeitung, welche Strategy anzuwenden ist

### Service

`<topic>Service.ts`

**Beispiel:**

`loggingService.ts` ... stellt Funktionen zu einem Thema bereit, dass sonst zu keinem Typ Composable passt

## Types

`<interface>.ts`

**Beispiel:**

`Wahlvorstand.ts` ... enthält das Interface `Wahlvorstand` und eine Hilfsklasse die das Interface implementiert

## Tests

Die Tests liegen im `tests`-Ordner, welcher der Aufbaustruktur des `src`-Ordners folgt.

`<Testgegenstand ohne Dateiendung>.spec.ts`

**Beispiel:**

`TheWahlvorstandMitgliederTable.spec.ts` ... Tests zu `TheWahlvorstandMitgliederTable.vue`

`wahlvorstandStore.spec.ts` ... Tests zu `wahlvorstandStore.ts`

## Storybook Stories

`<Storybookgegenstand ohne Dateiendung>.stories.ts`

> [!IMPORTANT]
> Stories zu Komponenten, die den Store verwenden, haben auf der Übersichtsseite (`docs`) alle denselben Zustand.
> Der Zustand für alle Komponenten entsprecht dem Zustand im Store mit der letzten Story.

**Beispiel:**

`BaseButtonRefresh.stories.ts` ... Stories zu `BaseButtonRefresh.vue`

## Testattribut

Damit Elemente leichter für Tests zugänglich sind, sollen sie ein Attribut für Tests bekommen.
Der Name des Attributes ist [`data-test`](https://docs.cypress.io/app/core-concepts/best-practices#Selecting-Elements).

Bei dem Wert handelt es sich **nicht** um eine ID.

## Eventhandlingmethoden

`on<Data><Event>`

**Beispiel:**

`onAnwesenheitChanged($event, mitglied)` ... Eventhandler, wenn sich die Anwesenheit geändert hat
