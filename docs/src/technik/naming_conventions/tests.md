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

```java
void dataFound() {}
void noDataFound() {}
void serviceCalled() {}
```

Damit der Gesamtcode im Projekt übersichtlicher und einheitlicher ist, sollen Naming Conventions eingesetzt werden.
Grundlage für die Einführung sind unter anderem auch sich wiederholende Tests mit gleichem Inhalt in den verschiedenen
Services. So wird gewährleistet, dass deren Kontext schneller klar ist, ohne den Code lesen zu müssen und die Wartung
und Erweiterung des Codes wird erleichtert.

## Beispiele

### Backend

```java
void should_returnDTO_when_givenValidId() {}
void should_notThrowException_when_newDataSaved() {}
void should_throwAccessDeniedException_whenAuthoritiesMissing() {}
```

#### Gruppierung überladener Methoden

**Backend-Beispiel:** Vereinfachter Pseudocode! Beispiel aus dem Vorfälle und Vorkommnisse Service.
::: code-group

```java{2-3,5-6,11-12} [MapperTest.java]
class EreignisModelMapperTest {
  @Nested
  class ToEntity {                      // Name der zu testenden Methode

     @Nested
     class ToEreignisEntity {           // nested overload 1
         @Test
         void should_returnEreignis_when_givenEreignisModel() {}
     }

     @Nested
     class ToListOfEreignisEntity {     // nested overload 2
         @Test
         void should_returnListOfEreignis_when_givenEreignisseWriteModel() {}
     }
  }
}
```

```java [Mapper.java]

@Mapper
public interface EreignisModelMapper {

    Ereignis toEntity(EreignisModel model);

    List<Ereignis> toEntity(EreignisseWriteModel model);
}
```

:::

### Frontend

Der Dateinamen von Tests sind [hier](/technik/naming_conventions/frontend#tests) beschrieben.

Allgemein gilt für den Aufbau:

```typescript
describe("<Dateiname des Testgegenstandes>", () => {
  describe("<zu testende Funktionalität>", () => {
    it("<Testfallbeschreibung>", () => {});
  });
});
```

> [!IMPORTANT]
> Die Tests sollen mit `it` definiert werden und **nicht** mit `test`, da `it` den Lesefluss verbessert.

#### Beispiel für Tests eines Stores

```typescript
import { describe, it } from "vitest";

/* Die Description ist der Dateiname des Testgegenstandes */ // [!code focus]
describe("wahlvorstandStore.ts", () => {// [!code focus]

  /* Die Description ist der Name der Funktion die getested wird */ // [!code focus]
  describe("isSchriftfuehrerAnwesend", () => { // [!code focus]
    /* Beschreibung des Testcases entsprechend des Schemas */ // [!code focus]
    it("should_returnFalse_when_noMitgliedExists", () => { // [!code focus]
    });

    it("should_returnTrue_when_atLeastOneMitgliedMatches", () => { // [!code focus] 
    });

    it("should_returnFalse_when_whenMitgliedWithFunktionExistsButIsNotAnwesend", () => {});

    it("should_returnFalse_when_noMitgliedMatchesFunktion", () => {});
  });

  describe("sendWahlvorstand", () => { // [!code focus]
    it("should_sendWahlvorstand_when_wahlbezirkIDIsGiven", async () => {});

    it("should_setLastSend_when_wahlvorstandIsSent", async () => {});

    it("should_notSendWahlvorstand_when_wahlbezirkIDIsNotGiven", async () => {});
  });
});
```

#### Beispiel für Tests zu einer Komponente

```typescript
import { describe, it } from "vitest";

/* Die Description ist der Dateiname des Testgegenstandes */ // [!code focus]
describe("TheWahlvorstandAnwesenheitRequirementCard.vue", () => { // [!code focus]

  /* Tests die sich mit dem Rendern der Komponente befassen */ // [!code focus]
  describe("visual logic", () => { // [!code focus]
    /* Tests entsprechend der Testcasebeschreibung */ // [!code focus]
    it("should_showNoErrorTexts_when_allRequirementsAreSatisfied", async (context) => { // [!code focus]
    });
    it("should_showErrorText_when_schriftfuehrerIsNotAnwesend", async (context) => {});
    it("should_showErrorText_when_wahlvorsteherIsNotAnwesend", async (context) => {});
  });

  /* Tests zu den Events der Komponente */ // [!code focus]
  describe("behavioral logic", () => { // [!code focus]
    /* Tests zu dem Event `update:model-value` */ // [!code focus]
    describe("update:model-value", () => { // [!code focus]
      /* Tests entsprechend der Testcasebeschreibung */ // [!code focus]
      it("should_setAnwesendTrue_when_checkBoxForMitgliedThatIsNotAnwesendWasClicked", async () => { // [!code focus]
      });

      it("should_setAnwesendFalse_when_checkBoxForMitgliedThatChangedToFalse", async () => {});
    });
  });
});
```
