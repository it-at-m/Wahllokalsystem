import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmabgabevermerke: vi.fn(),
  postStimmabgabevermerke: vi.fn(),
  createEmptyStimmabgabevermerke: vi.fn(),
}));

vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
      postStimmabgabevermerke: mockDefinitions.postStimmabgabevermerke,
    }),
  })
);
vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeUtils.ts",
  () => ({
    useStimmabgabevermerkeUtils: vi.fn().mockImplementation(() => ({
      createEmptyStimmabgabevermerke:
        mockDefinitions.createEmptyStimmabgabevermerke,
    })),
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
  const { generateRandomString, generateRandomNumber } =
    useCommonTestDataFactory();
  const { prepareUser } = useUserTestDataFactory();

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useStimmabgabevermerkeStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("loadStimmabgabevermerke", () => {
    it("should_addStimmabgabevermerkeToState_when_serviceReturnsData", async () => {
      const wahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(3);

      const existingStimmabgabevermerke = createStimmabgabevermerke();
      unitUnderTest.stimmabgabevermerke = [existingStimmabgabevermerke];

      const mockedServiceStimmabgabevermerke = createStimmabgabevermerke();
      mockDefinitions.getStimmabgabevermerke.mockResolvedValue(
        mockedServiceStimmabgabevermerke
      );

      await unitUnderTest.loadStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(unitUnderTest.stimmabgabevermerke).toStrictEqual([
        existingStimmabgabevermerke,
        mockedServiceStimmabgabevermerke,
      ]);
    });

    it("should_addDefaultStimmabgabevermerkeToState_when_serviceReturnsNoDataAndUserHasWahlbezirkID", async () => {
      const wahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(3);

      const existingStimmabgabevermerke = createStimmabgabevermerke();
      unitUnderTest.stimmabgabevermerke = [existingStimmabgabevermerke];

      mockDefinitions.getStimmabgabevermerke.mockResolvedValue(null);
      const mockedEmptyStimmabgabevermerke = createStimmabgabevermerke();
      mockDefinitions.createEmptyStimmabgabevermerke.mockReturnValue(
        mockedEmptyStimmabgabevermerke
      );

      useUserStore().setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: wahlbezirkID,
              wahlnummer: generateRandomString(1),
              wahlID: generateRandomString(10),
            },
          ])
          .build()
      );

      await unitUnderTest.loadStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(unitUnderTest.stimmabgabevermerke).toStrictEqual([
        existingStimmabgabevermerke,
        mockedEmptyStimmabgabevermerke,
      ]);
    });

    it("should_notAddDefaultStimmabgabevermerkeToState_when_serviceReturnsNoDataButUserHasNotThatWahlbezirkID", async () => {
      const wahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(3);

      const existingStimmabgabevermerke = createStimmabgabevermerke();
      unitUnderTest.stimmabgabevermerke = [existingStimmabgabevermerke];

      mockDefinitions.postStimmabgabevermerke.mockResolvedValue(null);
      const mockedEmptyStimmabgabevermerke = createStimmabgabevermerke();
      mockDefinitions.createEmptyStimmabgabevermerke.mockReturnValue(
        mockedEmptyStimmabgabevermerke
      );

      useUserStore().setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: wahlbezirkID + wahlbezirkID,
              wahlnummer: generateRandomString(1),
              wahlID: generateRandomString(10),
            },
          ])
          .build()
      );

      await unitUnderTest.loadStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(unitUnderTest.stimmabgabevermerke).toStrictEqual([
        existingStimmabgabevermerke,
      ]);
    });

    it.each([{ sendNotification: true }, { sendNotification: false }])(
      'should_callServiceWithSendNotification"$sendNotification"_when_notificationParameterIsUsed',
      async (argument) => {
        const wahlbezirkID = "wahlbezirkID";
        const waehlerverzeichnisNummer = 1;

        await unitUnderTest.loadStimmabgabevermerke(
          wahlbezirkID,
          waehlerverzeichnisNummer,
          argument.sendNotification
        );

        expect(mockDefinitions.getStimmabgabevermerke.mock.calls).toStrictEqual(
          [[wahlbezirkID, waehlerverzeichnisNummer, argument.sendNotification]]
        );
      }
    );
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

  describe("getBlattnummernThatPreventDeletion", () => {
    it.each([
      {
        description: "should_returnEmptyArray_when_rowsToDeleteAreEmpty",
        wahldaten: prepareWahldaten()
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
          .build(),
        expectedSize: 0,
        expectedBlattnummern: [],
      },
      {
        description: "should_returnBlattnummern_when_allRowsToDeleteAreFilled",
        wahldaten: prepareWahldaten()
          .vermerke([
            prepareVermerk().blattnummer(2).build(),
            prepareVermerk().blattnummer(3).build(),
            prepareVermerk().blattnummer(4).build(),
          ])
          .build(),
        expectedSize: 2,
        expectedBlattnummern: [3, 4],
      },
      {
        description: "should_returnBlattnummern_when_someRowsToDeleteAreFilled",
        wahldaten: prepareWahldaten()
          .vermerke([
            prepareVermerk().blattnummer(2).build(),
            prepareVermerk()
              .blattnummer(3)
              .stimmzettel([prepareStimmzettel().anzahl(null).build()])
              .build(),
            prepareVermerk().blattnummer(4).build(),
          ])
          .build(),
        expectedSize: 1,
        expectedBlattnummern: [4],
      },
    ])("$description", (testCaseParameter) => {
      const stimmabgabevermerk = prepareStimmabgabevermerke()
        .wahldaten([testCaseParameter.wahldaten])
        .build();
      unitUnderTest.stimmabgabevermerke = [stimmabgabevermerk];

      const result = unitUnderTest.getBlattnummernThatPreventDeletion(2);

      expect(result.length).toBe(testCaseParameter.expectedSize);
      expect(result).toEqual(testCaseParameter.expectedBlattnummern);
    });

    it("should_returnBlattnummern_when_someRowsToDeleteAreFilledInDifferentStimmabgabevermerke", () => {
      const wahldatenOne = prepareWahldaten()
        .vermerke([
          prepareVermerk().blattnummer(2).build(),
          prepareVermerk()
            .blattnummer(3)
            .stimmzettel([prepareStimmzettel().anzahl(null).build()])
            .build(),
          prepareVermerk().blattnummer(4).build(),
        ])
        .build();
      const stimmabgabevermerkeOne = prepareStimmabgabevermerke()
        .wahldaten([wahldatenOne])
        .build();
      const wahldatenTwo = prepareWahldaten()
        .vermerke([
          prepareVermerk().blattnummer(2).build(),
          prepareVermerk().blattnummer(3).build(),
          prepareVermerk()
            .blattnummer(4)
            .stimmzettel([prepareStimmzettel().anzahl(null).build()])
            .build(),
        ])
        .build();
      const stimmabgabevermerkeTwo = prepareStimmabgabevermerke()
        .wahldaten([wahldatenTwo])
        .build();
      unitUnderTest.stimmabgabevermerke = [
        stimmabgabevermerkeOne,
        stimmabgabevermerkeTwo,
      ];

      const result = unitUnderTest.getBlattnummernThatPreventDeletion(2);

      expect(result.length).toBe(2);
      expect(result).toEqual([3, 4]);
    });
  });

  describe("changeRowCount", () => {
    it("should_decreaseRowCount_when_enteredNumberIsLowerThanActual", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

      unitUnderTest.changeRowCount(2);

      unitUnderTest.stimmabgabevermerke.forEach((stimmabgabevermerke) => {
        expect(stimmabgabevermerke.wahldaten[0]?.vermerke.length).toBe(1);
      });
    });

    it("should_increaseRowCount_when_enteredNumberIsHigherThanActual", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

      unitUnderTest.changeRowCount(5);

      unitUnderTest.stimmabgabevermerke.forEach((stimmabgabevermerke) => {
        expect(stimmabgabevermerke.wahldaten[0]?.vermerke.length).toBe(4);

        const expectedBlattnummern = [2, 3, 4, 5];

        stimmabgabevermerke.wahldaten[0]?.vermerke?.forEach(
          (vermerk, index) => {
            expect(vermerk.blattnummer).toBe(expectedBlattnummern[index]);
          }
        );
      });
    });

    it("should_notIncreaseOrDecrease_when_enteredNumberIsSameThanActual", () => {
      unitUnderTest.stimmabgabevermerke = [createStimmabgabevermerke()];

      unitUnderTest.changeRowCount(3);

      unitUnderTest.stimmabgabevermerke.forEach((stimmabgabevermerke) => {
        expect(stimmabgabevermerke.wahldaten[0]?.vermerke.length).toBe(2);
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
    it("should_saveStimmabgabevermerke_when_called", async () => {
      const stimmabgabevermerke = createStimmabgabevermerke();

      mockDefinitions.postStimmabgabevermerke.mockReturnValue(
        Promise.resolve(null)
      );

      unitUnderTest.stimmabgabevermerke = [stimmabgabevermerke];

      await unitUnderTest.saveStimmabgabevermerke();

      expect(mockDefinitions.postStimmabgabevermerke).toHaveBeenCalledWith(
        stimmabgabevermerke.wahlbezirkID,
        stimmabgabevermerke.waehlerverzeichnisNummer,
        stimmabgabevermerke
      );
    });
  });
});
