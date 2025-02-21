# Naming Convention für Frontend Elemente

bitte den [StyleGuide](https://vuejs.org/style-guide/) beachten.

- Dateinamen sind in [c|C]amelCase
- Definitionen von Properties, Events und lokalen Variablen in camelCase
- Element-Attribute in Kebab-Case

## Views

`<Domain>[<Funktion>]View.vue`

**Beispiel**

`WahlvorstandView.vue` ... eine View für den Wahlvorstand

## Components

### Basiskomponenten

`Base<Type>[Domain]<Funktion>.vue`

**Beispiel**

`BaseButtonRefresh.vue` ... Allgemeiner Button zur Aktualisierung

`BaseButtonSave.vue` ... Allgemeiner Button zum Speichern

### Single-Use-Komponenten

`The[Domain]<Funktion><Type>.vue`

**Beispiele**

`TheWahlvorstandLastSendDiv.vue` ... Wahlvorstandskomponente welche die Uhrzeit der letzten Übermittlung darstellt

`TheWahlvorstandMitgliederTable.vue` ... Wahlvorstandskomponente welche Mitglieder des Wahlvorstandes als Tabelle anzeigt

## Stores

`<domain>Store.ts`

**Beispiel**

`wahlvorstandStore.ts` ... Store für den Wahlvorstand

## composables

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

`wahlvorstand.ts` ... enthält das Interface `Wahlvorstand` und eine Hilfsklasse die das Interface implementiert

## Tests

Die Tests liegen parallel zum Testgegenstand.

`<Testgegenstand ohne Dateiendung>.spec.ts`

**Beispiel**

`TheWahlvorstandMitgliederTable.spec.ts` ... Tests zu `TheWahlvorstandMitgliederTable.vue`

`wahlvorstandStore.spec.ts` ... Tests zu `wahlvorstandStore.ts`

## Storybook Stories

`<Storybookgegenstand ohne Dateiendung>.stories.ts`

**Beispiel**

`BaseButtonRefresh.stories.ts` ... Stories zu `BaseButtonRefresh.vue`

## Testattribut

Damit Element leichter für Tests zugänglich sollen sie ein Attribut für Tests bekommen.
Der Name des Attributes ist [`data-cy`](https://docs.cypress.io/app/core-concepts/best-practices#Selecting-Elements).

Bei dem Wert handelt es sich **nicht** um eine ID.

## Eventhandlingmethoden

`on<Data><Event>`

**Beipspiel**

`onAnwesenheitChanged($event, mitglied)` ... Eventhandler wenn sich die Anwesenheit geändert hat 