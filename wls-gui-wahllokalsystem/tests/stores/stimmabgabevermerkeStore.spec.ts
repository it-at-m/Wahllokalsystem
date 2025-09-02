import { createTestingPinia } from "@pinia/testing";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postStimmabgabevermerke: vi.fn(),
}));

vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      postStimmabgabevermerke: mockDefinitions.postStimmabgabevermerke,
    }),
  })
);

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
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

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
      const stimmabgabevermerkeOne = prepareStimmabgabevermerke()
        .wahldaten([wahldaten])
        .build();
      const stimmabgabevermerkeTwo = prepareStimmabgabevermerke()
        .wahldaten([wahldaten])
        .build();
      unitUnderTest.stimmabgabevermerke = [
        stimmabgabevermerkeOne,
        stimmabgabevermerkeTwo,
      ];
      const result = unitUnderTest.isAnyRowThatShouldBeDeletedFilled(2);
      expect(result).toBe(false);
    });
  });

  describe("changeRowCount", () => {
    it("should_decreaseRowCount_when_enteredNumberIsLowerThanActual", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

      unitUnderTest.changeRowCount(2);

      unitUnderTest.stimmabgabevermerke.forEach((stimmabgabevermerke) => {
        expect(stimmabgabevermerke.wahldaten[0].vermerke.length).toBe(1);
      });
    });

    it("should_increaseRowCount_when_enteredNumberIsHigherThanActual", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

      unitUnderTest.changeRowCount(5);

      unitUnderTest.stimmabgabevermerke.forEach((stimmabgabevermerke) => {
        expect(stimmabgabevermerke.wahldaten[0].vermerke.length).toBe(4);
      });
    });

    it("should_notIncreaseOrDecrease_when_enteredNumberIsSameThanActual", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

      unitUnderTest.changeRowCount(3);

      unitUnderTest.stimmabgabevermerke.forEach((stimmabgabevermerke) => {
        expect(stimmabgabevermerke.wahldaten[0].vermerke.length).toBe(2);
      });
    });
  });

  describe("lowestNumberOfRowsOverAllWahldaten", () => {
    it("should_calculateTheCorrectNumber_when_allWahldatenHaveSameNumberOfVermerke", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

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
      const stimmabgabevermerkeOne = prepareStimmabgabevermerke()
        .wahldaten([wahldatenOne])
        .build();
      const stimmabgabevermerkeTwo = prepareStimmabgabevermerke()
        .wahldaten([wahldatenTwo])
        .build();
      unitUnderTest.stimmabgabevermerke = [
        stimmabgabevermerkeOne,
        stimmabgabevermerkeTwo,
      ];

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

      const stimmabgabevermerkeOne = prepareStimmabgabevermerke()
        .wahldaten([wahldatenOne])
        .build();
      const stimmabgabevermerkeTwo = prepareStimmabgabevermerke()
        .wahldaten([wahldatenTwo])
        .build();

      unitUnderTest.stimmabgabevermerke = [
        stimmabgabevermerkeOne,
        stimmabgabevermerkeTwo,
      ];

      const result = unitUnderTest.stimmabgabevermerkeTableTotalEachWahldaten;

      expect(result).toStrictEqual([6, 26]);
    });
  });

  describe("sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl", () => {
    it("should_returnMapWithCalculatedValuesForEachWahl_when_givenStimmabgabevermerke", () => {
      const wahldatenOne = prepareWahldaten()
        .vermerke([
          prepareVermerk()
            .blattnummer(2)
            .stimmzettel([prepareStimmzettel().anzahl(10).build()])
            .build(),
          prepareVermerk()
            .blattnummer(3)
            .stimmzettel([prepareStimmzettel().anzahl(20).build()])
            .build(),
          prepareVermerk()
            .blattnummer(4)
            .stimmzettel([prepareStimmzettel().anzahl(30).build()])
            .build(),
        ])
        .eingenommeneWahlscheine(
          new Map([[StimmzettelStimmzettelartEnum.Klein, 20]])
        )
        .build();

      const wahldatenTwo = prepareWahldaten()
        .vermerke([
          prepareVermerk()
            .blattnummer(2)
            .stimmzettel([prepareStimmzettel().anzahl(5).build()])
            .build(),
          prepareVermerk()
            .blattnummer(3)
            .stimmzettel([prepareStimmzettel().anzahl(15).build()])
            .build(),
          prepareVermerk()
            .blattnummer(4)
            .stimmzettel([prepareStimmzettel().anzahl(25).build()])
            .build(),
        ])
        .eingenommeneWahlscheine(
          new Map([[StimmzettelStimmzettelartEnum.Klein, 40]])
        )
        .build();

      const stimmabgabevermerkeOne = prepareStimmabgabevermerke()
        .wahldaten([wahldatenOne])
        .build();
      const stimmabgabevermerkeTwo = prepareStimmabgabevermerke()
        .wahldaten([wahldatenTwo])
        .build();

      unitUnderTest.stimmabgabevermerke = [
        stimmabgabevermerkeOne,
        stimmabgabevermerkeTwo,
      ];

      const result =
        unitUnderTest.sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl;

      expect(result.get(wahldatenOne.wahlID)).toStrictEqual(80);
      expect(result.get(wahldatenTwo.wahlID)).toStrictEqual(85);
    });
  });

  describe("saveStimmabgabevermerke", () => {
    it("should_saveStimmabgabevermerke_when_called", () => {
      const stimmabgabevermerke = createStimmabgabevermerke();

      mockDefinitions.postStimmabgabevermerke.mockReturnValue(
        Promise.resolve(null)
      );

      unitUnderTest.stimmabgabevermerke = [stimmabgabevermerke];

      unitUnderTest.saveStimmabgabevermerke();

      expect(mockDefinitions.postStimmabgabevermerke).toHaveBeenCalledWith(
        stimmabgabevermerke.wahlbezirkID,
        stimmabgabevermerke.waehlerverzeichnisNummer,
        stimmabgabevermerke
      );
    });
  });
});
