# Naming Convention für Frontend Elemente

Bitte den [StyleGuide](https://vuejs.org/style-guide/) beachten.

- Dateinamen sind in [c|C]amelCase
- Definitionen von Properties, Events und lokalen Variablen in camelCase
- Element-Attribute in Kebab-Case

Die Benennung erfolgt grundsätzlich auf Englisch, mit Ausnahme von Fachbegriffen welche auf Deutsch verwendet werden.

## Views

`<Domain>[<Funktion>]View.vue`

**Beispiel**

`WahlvorstandView.vue` ... eine View für den Wahlvorstand

## Components

### Basiskomponenten

`Base<Type>[<Domain>]<Funktion>.vue`

**Beispiel**

`BaseButtonRefresh.vue` ... Allgemeiner Button zur Aktualisierung

`BaseButtonSave.vue` ... Allgemeiner Button zum Speichern

`BaseWahlvorstandMitgliedCard.vue` ... V-Card zur Darstellung eines Wahlvorstandsmitgliedes

### Single-Use-Komponenten

`The[<Domain>]<Funktion><Type>.vue`

**Beispiele**

`TheWahlvorstandLastSendDiv.vue` ... Wahlvorstandskomponente welche die Uhrzeit der letzten Übermittlung darstellt

`TheWahlvorstandMitgliederTable.vue` ... Wahlvorstandskomponente welche Mitglieder des Wahlvorstandes als Tabelle anzeigt

## Stores

`<domain>Store.ts`

**Beispiel**

`wahlvorstandStore.ts` ... Store für den Wahlvorstand

## Composables

### domainspezifische composables

`<domain><Funktion>.ts`

**Beispiel**

`wahlvorstandService.ts` ... fachliche Funktionen zum Wahlvorstand

`wahlvorstandMapper.ts` ... Mappingfunktionen zum Wahlvorstand

`wahlvorstandRules.ts` ... wahlvorstandspezifische Validierungsregeln

### allgemeine composables

`<funktion>.ts`

**Beispiel**

`formatter.ts` ... allgemein gültige Formatierungsfunktionen 

## Types

`<interface>.ts`

**Beispiel**

`Wahlvorstand.ts` ... enthält das Interface `Wahlvorstand` und eine Hilfsklasse die das Interface implementiert

## Tests

Die Tests liegen im `tests`-Ordner, welcher der Aufbaustruktur des `src`-Ordners folgt.

`<Testgegenstand ohne Dateiendung>.spec.ts`

**Beispiel**

`TheWahlvorstandMitgliederTable.spec.ts` ... Tests zu `TheWahlvorstandMitgliederTable.vue`

`wahlvorstandStore.spec.ts` ... Tests zu `wahlvorstandStore.ts`

## Storybook Stories

`<Storybookgegenstand ohne Dateiendung>.stories.ts`

> [!IMPORTANT]
> Stories zu Komponenten, die den Stories verwenden, haben auf der Übersichtsseite (`docs`) alle denselben Zustand.
> Der Zustand für alle Komponenten entsprecht dem Zustand im Store mit der letzten Story.

**Beispiel**

`BaseButtonRefresh.stories.ts` ... Stories zu `BaseButtonRefresh.vue`

## Testattribut

Damit Elemente leichter für Tests zugänglich sind, sollen sie ein Attribut für Tests bekommen.
Der Name des Attributes ist [`data-test`](https://docs.cypress.io/app/core-concepts/best-practices#Selecting-Elements).

Bei dem Wert handelt es sich **nicht** um eine ID.

## Eventhandlingmethoden

`on<Data><Event>`

**Beipspiel**

`onAnwesenheitChanged($event, mitglied)` ... Eventhandler, wenn sich die Anwesenheit geändert hat 