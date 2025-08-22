import { createTestingPinia } from "@pinia/testing";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";

describe("stimmabgabevermerkeStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useStimmabgabevermerkeStore>;

  const {
    createStimmabgabevermerke,
    prepareWahldaten,
    prepareStimmabgabevermerke,
    prepareVermerk,
    prepareStimmzettel,
  } = useStimmabgabevermerkeTestDataFactory();

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useStimmabgabevermerkeStore(testPinia);
  });

  describe("isAnyRowThatShouldBeDeletedFilled", () => {
    it("should_returnTrue_when_rowsToRemoveContainLegitValues", () => {
      unitUnderTest.stimmabgabevermerke = createStimmabgabevermerke();

      const result = unitUnderTest.isAnyRowThatShouldBeDeletedFilled(2);
      expect(result).toBe(true);
    });

    it("should_returnFalse_when_rowsToRemoveContainOnlyNullOrZero", () => {
      const wahldaten = prepareWahldaten()
        .vermerke([
          prepareVermerk().blattnummer(2).build(),
          prepareVermerk()
            .blattnummer(3)
            .stimmzettel([prepareStimmzettel().anzahl(null).build()])
            .build(),
          prepareVermerk()
            .blattnummer(4)
            .stimmzettel([prepareStimmzettel().anzahl(0).build()])
            .build(),
        ])
        .build();
      unitUnderTest.stimmabgabevermerke = prepareStimmabgabevermerke()
        .wahldaten(new Set([wahldaten, wahldaten]))
        .build();
      const result = unitUnderTest.isAnyRowThatShouldBeDeletedFilled(2);
      expect(result).toBe(false);
    });
  });

  describe("changeRowCount", () => {
    it("should_decreaseRowCount_when_enteredNumberIsLowerThanActual", () => {
      unitUnderTest.stimmabgabevermerke = createStimmabgabevermerke();

      unitUnderTest.changeRowCount(2);

      unitUnderTest.stimmabgabevermerke.wahldaten.forEach((wahldaten) => {
        expect(wahldaten.vermerke.length).toBe(1);
      });
    });

    it("should_increaseRowCount_when_enteredNumberIsHigherThanActual", () => {
      unitUnderTest.stimmabgabevermerke = createStimmabgabevermerke();

      unitUnderTest.changeRowCount(5);

      unitUnderTest.stimmabgabevermerke.wahldaten.forEach((wahldaten) => {
        expect(wahldaten.vermerke.length).toBe(4);
      });
    });

    it("should_notIncreaseOrDecrease_when_enteredNumberIsSameThanActual", () => {
      unitUnderTest.stimmabgabevermerke = createStimmabgabevermerke();

      unitUnderTest.changeRowCount(3);

      unitUnderTest.stimmabgabevermerke.wahldaten.forEach((wahldaten) => {
        expect(wahldaten.vermerke.length).toBe(2);
      });
    });
  });

  describe("lowestNumberOfRowsOverAllWahldaten", () => {
    it("should_calculateTheCorrectNumber_when_allWahldatenHaveSameNumberOfVermerke", () => {
      unitUnderTest.stimmabgabevermerke = createStimmabgabevermerke();

      const result = unitUnderTest.lowestNumberOfRowsOverAllWahldaten;

      expect(result).toBe(2);
    });

    it("should_calculateTheLowest_when_allWahldatenHaveDifferentNumberOfVermerke", () => {
      const wahldatenOne = prepareWahldaten()
        .vermerke([
          prepareVermerk().blattnummer(2).build(),
          prepareVermerk().blattnummer(3).build(),
          prepareVermerk().blattnummer(4).build(),
        ])
        .build();

      const wahldatenTwo = prepareWahldaten()
        .vermerke([
          prepareVermerk().blattnummer(2).build(),
          prepareVermerk().blattnummer(3).build(),
          prepareVermerk().blattnummer(4).build(),
          prepareVermerk().blattnummer(5).build(),
        ])
        .build();

      unitUnderTest.stimmabgabevermerke = prepareStimmabgabevermerke()
        .wahldaten(new Set([wahldatenOne, wahldatenTwo]))
        .build();

      const result = unitUnderTest.lowestNumberOfRowsOverAllWahldaten;

      expect(result).toBe(3);
    });
  });
  describe("stimmabgabevermerkeTableTotalEachWahldaten", () => {
    it("should_calculateCorrectArrayOfTotalVermerkeForWahldaten_when_givenStimmabgabevermerke", () => {
      const wahldatenOne = prepareWahldaten()
        .vermerke([
          prepareVermerk()
            .blattnummer(2)
            .stimmzettel([prepareStimmzettel().anzahl(1).build()])
            .build(),
          prepareVermerk()
            .blattnummer(3)
            .stimmzettel([prepareStimmzettel().anzahl(2).build()])
            .build(),
          prepareVermerk()
            .blattnummer(4)
            .stimmzettel([prepareStimmzettel().anzahl(3).build()])
            .build(),
        ])
        .build();

      const wahldatenTwo = prepareWahldaten()
        .vermerke([
          prepareVermerk()
            .blattnummer(2)
            .stimmzettel([prepareStimmzettel().anzahl(5).build()])
            .build(),
          prepareVermerk()
            .blattnummer(3)
            .stimmzettel([prepareStimmzettel().anzahl(8).build()])
            .build(),
          prepareVermerk()
            .blattnummer(4)
            .stimmzettel([prepareStimmzettel().anzahl(13).build()])
            .build(),
        ])
        .build();

      unitUnderTest.stimmabgabevermerke = prepareStimmabgabevermerke()
        .wahldaten(new Set([wahldatenOne, wahldatenTwo]))
        .build();

      const result = unitUnderTest.stimmabgabevermerkeTableTotalEachWahldaten;

      expect(result).toStrictEqual([6, 26]);
    });
  });
});
