# Namingconventions für Tests im Frontend

Der Dateinamen von Tests ist [hier](../frontend#tests) beschrieben.

Tests werden zu Suits zusammengefasst welche durch [`describe`](https://vitest.dev/api/#describe) definiert werden.

Allgemein gilt für den Aufbau:

```typescript
describe("<Dateiname des Testgegenstandes>", () => {
    describe("<zu testende Funktionalität>", () => {
        it("<Testfallbeschreibung>", () => {
        });
    });
});
```

Die Testfallbeschreibung ist nach dem System `should_<result>_when_<input>` zu verfassen, wie beim
[backend](backend#entscheidung).

## Beispiel für Tests eines Stores

```typescript
import { afterEach, beforeEach, describe, it } from "vitest";

/* Die Description ist der Dateiname des Testgegenstandes */ // [!code focus]
describe("wahlvorstandStore.ts", () => { // [!code focus]    

  /* Die Description ist der Name der Funktion die getested wird */ // [!code focus]
  describe("isSchriftfuehrerAnwesend", () => { // [!code focus]
    /* Beschreibung des Testcases entsprechend des Schemas */ // [!code focus]  
    it("should_returnFalse_when_noMitgliedExists", () => { // [!code focus]
    });

    it("should_returnTrue_when_atLeastOneMitgliedMatches", () => { // [!code focus]
    });

    it("should_returnFalse_when_whenMitgliedWithFunktionExistsButIsNotAnwesend", () => {
    });

    it("should_returnFalse_when_noMitgliedMatchesFunktion", () => {
    });
  });

  describe("sendWahlvorstand", () => { // [!code focus]
    it("should_sendWahlvorstand_when_wahlbezirkIDIsGiven", async () => {
    });

    it("should_setLastSend_when_wahlvorstandIsSent", async () => {
    });

    it("should_notSendWahlvorstand_when_wahlbezirkIDIsNotGiven", async () => {
    });
  });
});
```

## Beispiel für Tests zu einer Komponente

```typescript
/* Die Description ist der Dateiname des Testgegenstandes */ // [!code focus]
describe("TheWahlvorstandAnwesenheitRequirementCard.vue", () => { // [!code focus]
   
  /* Tests die sich mit dem Rendern der Komponente befassen */ // [!code focus]
  describe("Rendering", () => { // [!code focus]
    /* Tests entsprechend der Testcasebeschreibung */ // [!code focus]
    it("should_showNoErrorTexts_when_allRequirementsAreSatisfied", async (context) => { // [!code focus]
    });
    it("should_showErrorText_when_schriftfuehrerIsNotAnwesend", async (context) => {
    });
    it("should_showErrorText_when_wahlvorsteherIsNotAnwesend", async (context) => {
    });
  });
  
  /* Tests zu den Events der Komponente */ // [!code focus]
  describe("Eventing", () => { // [!code focus]
      /* Tests zu dem Event `update:model-value` */ // [!code focus]
      describe("update:model-value", () => { // [!code focus]
        /* Tests entsprechend der Testcasebeschreibung */ // [!code focus]
        it("should_setAnwesendTrue_when_checkBoxForMitgliedThatIsNotAnwesendWasClicked", async () => { // [!code focus]
        });

        it("should_setAnwesendFalse_when_checkBoxForMitgliedThatChangedToFalse", async () => {
        }); 
      });
  });
});

```