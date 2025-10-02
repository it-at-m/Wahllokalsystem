import type { Wahl } from "@/types/wahl/Wahl.ts";

import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
  postBeanstandeteWahlbriefe: vi.fn(),
  getBeanstandeteWahlbriefe: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlService.ts", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));
vi.mock("@/composables/briefwahl/briefwahlService.ts", () => ({
  useBriefwahlService: () => ({
    postBeanstandeteWahlbriefe: mockDefinitions.postBeanstandeteWahlbriefe,
    getBeanstandeteWahlbriefe: mockDefinitions.getBeanstandeteWahlbriefe,
  }),
}));
vi.mock(
  "@/composables/ergebnisermittlung/ergebnisermittlungService.ts",
  () => ({
    useErgebnisermittlungService: () => ({
      getStimmzettelumschlaege: mockDefinitions.getStimmzettelumschlaege,
    }),
  })
);

const { createWahl, prepareWahl } = useWahlTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareBeanstandeteWahlbriefe } =
  useBeanstandeteWahlbriefeTestDataFactory();
const { createStimmzettelumschlaege } =
  useStimmzettelumschlaegeTestDataFactory();

const mockedNow = new Date();

describe("wahlenStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlenStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useWahlenStore();

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("initWahlen", () => {
    it("should_loadAndSortWahlen_when_calledWithCorrectWahltagID", async () => {
      const wahltagID = generateRandomString(10);
      const userStore = useUserStore();

      const wahl1 = prepareWahl().nummer("0").build();
      const wahl2 = prepareWahl().nummer("0").build();
      userStore.setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlMetaData([
            {
              wahlID: wahl1.wahlID,
              wahlnummer: "1",
              wahlbezirkID: generateRandomString(10),
            },
            {
              wahlID: wahl2.wahlID,
              wahlnummer: "0",
              wahlbezirkID: generateRandomString(10),
            },
          ])
          .build()
      );
      const mockedWahlArrayFromService = [wahl1, wahl2];
      const expectedSortedWahlArray = [wahl2, wahl1];

      mockDefinitions.getWahlen.mockReturnValue(
        Promise.resolve(mockedWahlArrayFromService)
      );

      await unitUnderTest.wahlenActions.initWahlen();

      expect(unitUnderTest.wahlenState.wahlen).toStrictEqual(
        expectedSortedWahlArray
      );
      expect(wahl1.nummer).toStrictEqual("1");
      expect(wahl2.nummer).toStrictEqual("0");
    });
  });

  describe("waehlerverzeichnisNummern", () => {
    it("should_returnEmptyList_when_wahlenDoNotExist", () => {
      expect(
        unitUnderTest.waehlerverzeichnisGetter.waehlerverzeichnisNummern
      ).toStrictEqual([]);
    });

    it("should_returnListOfWvzNummern_when_wahlenExist", () => {
      unitUnderTest.wahlenState.wahlen = [
        prepareWahl().waehlerverzeichnisNummer(1).build(),
        prepareWahl().waehlerverzeichnisNummer(2).build(),
      ];

      expect(
        unitUnderTest.waehlerverzeichnisGetter.waehlerverzeichnisNummern
      ).toStrictEqual([1, 2]);
    });
  });

  describe("getWaehlerverzeichnisNummerOrUndefinedById", () => {
    it("should_returnWaehlerverzeichnisNummer_when_calledWithId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result =
        unitUnderTest.waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
          wahlTwo.wahlID
        );

      expect(result).toStrictEqual(wahlTwo.waehlerverzeichnisNummer);
    });

    it("should_returnUndefined_when_calledWithIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo];

      const result =
        unitUnderTest.waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
          wahlThree.wahlID
        );

      expect(result).toBeUndefined();
    });
  });

  describe("getWahlNameOrBlankStringById", () => {
    it("should_getWahlName_when_calledWithWahlId", async () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.wahlenActions.getWahlNameOrBlankStringById(
        wahlOne.wahlID
      );

      expect(result).toStrictEqual(wahlOne.name);
    });
  });

  describe("getWahlTagOrBlankStringById", () => {
    it("should_getWahlTag_when_calledWithWahlId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.wahlenActions.getWahlTagOrBlankStringById(
        wahlOne.wahlID
      );

      expect(result).toStrictEqual(wahlOne.wahltag);
    });

    it("should_getBlank_when_calledWithWahlIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();
      const wahlFour = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.wahlenActions.getWahlTagOrBlankStringById(
        wahlFour.wahlID
      );

      expect(result).toStrictEqual("");
    });
  });

  describe("getWahlOrUndefinedById", () => {
    it("should_getWahl_when_calledWithWahlId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.wahlenActions.getWahlOrUndefinedById(
        wahlOne.wahlID
      );

      expect(result).toStrictEqual(wahlOne);
    });

    it("should_getUndefined_when_calledWithWahlIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();
      const wahlFour = createWahl();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.wahlenActions.getWahlOrUndefinedById(
        wahlFour.wahlID
      );

      expect(result).toBeUndefined();
    });

    it("should_getUndefined_when_wahlenDoNotExist", () => {
      unitUnderTest.wahlenState.wahlen = null;

      const result =
        unitUnderTest.wahlenActions.getWahlOrUndefinedById("testId");

      expect(result).toBeUndefined();
    });
  });

  describe("getWahlIdOrUndefinedByWahlart", () => {
    it("should_getWahlId_when_calledWithWahlArt", () => {
      const expectedWahlID = "wahlIdOBW";
      const wahlOne = prepareWahl()
        .wahlID(expectedWahlID)
        .wahlart(WahlWahlartEnum.Obw)
        .build();
      const wahlTwo = prepareWahl()
        .wahlID("wahlIdSRW")
        .wahlart(WahlWahlartEnum.Srw)
        .build();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo];

      const result = unitUnderTest.wahlenActions.getWahlIdOrUndefinedByWahlart(
        WahlWahlartEnum.Obw
      );

      expect(result).toBe(expectedWahlID);
    });

    it("should_getUndefined_when_calledWithWahlArtThatDoesNotExistInWahlen", () => {
      const wahlOne = prepareWahl().wahlart(WahlWahlartEnum.Obw).build();
      const wahlTwo = prepareWahl().wahlart(WahlWahlartEnum.Srw).build();

      unitUnderTest.wahlenState.wahlen = [wahlOne, wahlTwo];

      const result = unitUnderTest.wahlenActions.getWahlIdOrUndefinedByWahlart(
        WahlWahlartEnum.Mbw
      );

      expect(result).toBeUndefined();
    });

    it("should_getUndefined_when_wahlenDoNotExist", () => {
      unitUnderTest.wahlenState.wahlen = null;

      const result = unitUnderTest.wahlenActions.getWahlIdOrUndefinedByWahlart(
        WahlWahlartEnum.Obw
      );

      expect(result).toBeUndefined();
    });
  });

  describe("initBeanstandeteWahlbriefe", () => {
    it("should_loadBeanstandeteWahlbriefeAndAddThemToCorrespondingWahlen_when_called", async () => {
      const wahlbezirkID = "wahlbezirkId";
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const wahlID = "wahlID";
      const wvzNr = 1;

      const mockedBeanstandeteWahlbriefe = prepareBeanstandeteWahlbriefe()
        .wahlbezirkID(wahlbezirkID)
        .waehlerverzeichnisNummer(wvzNr)
        .beanstandeteWahlbriefe(new Map([[wahlID, ["ZUGELASSEN"]]]))
        .build();

      mockDefinitions.getBeanstandeteWahlbriefe.mockReturnValue(
        mockedBeanstandeteWahlbriefe
      );

      unitUnderTest.wahlenState.wahlen = [
        prepareWahl()
          .wahlID(wahlID)
          .waehlerverzeichnisNummer(wvzNr)
          .beanstandeteWahlbriefe([])
          .build(),
      ];

      expect(
        unitUnderTest.wahlenState.wahlen[0].beanstandeteWahlbriefe
      ).toStrictEqual([]);

      await unitUnderTest.beanstandeteWahlbriefeActions.initBeanstandeteWahlbriefe();

      expect(
        unitUnderTest.wahlenState.wahlen[0].beanstandeteWahlbriefe
      ).toStrictEqual(["ZUGELASSEN"]);
    });

    it("should_notCallService_when_noWahlenGiven", async () => {
      unitUnderTest.wahlenState.wahlen = [];

      await unitUnderTest.beanstandeteWahlbriefeActions.initBeanstandeteWahlbriefe();

      expect(mockDefinitions.getBeanstandeteWahlbriefe).not.toHaveBeenCalled();
    });
  });

  describe("addBeanstandeterWahlbriefEntry", () => {
    it("should_addBeanstandeteWahlbriefeEntryInStore_when_addWahlbriefIsCalled", () => {
      unitUnderTest.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
          .build(),
      ];

      expect(
        unitUnderTest.wahlenState.wahlen[0].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG"]);
      expect(
        unitUnderTest.wahlenState.wahlen[1].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG"]);

      unitUnderTest.beanstandeteWahlbriefeActions.addBeanstandeterWahlbriefEntry();

      expect(
        unitUnderTest.wahlenState.wahlen[0].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG", null]);
      expect(
        unitUnderTest.wahlenState.wahlen[1].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG", null]);
    });
  });

  describe("deleteBeanstandeterWahlbriefEntry", () => {
    it("should_deleteBeanstandeterWahlbriefEntryFromWahlen_when_called", () => {
      unitUnderTest.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG", "ZUGELASSEN"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG", "ZUGELASSEN"])
          .build(),
      ];

      expect(
        unitUnderTest.wahlenState.wahlen[0].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG", "ZUGELASSEN"]);
      expect(
        unitUnderTest.wahlenState.wahlen[1].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG", "ZUGELASSEN"]);

      unitUnderTest.beanstandeteWahlbriefeActions.deleteBeanstandeterWahlbriefEntry(
        1
      );

      expect(
        unitUnderTest.wahlenState.wahlen[0].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG"]);
      expect(
        unitUnderTest.wahlenState.wahlen[1].beanstandeteWahlbriefe
      ).toStrictEqual(["GEGENSTAND_IM_UMSCHLAG"]);
    });
  });

  describe("saveBeanstandeteWahlbriefe", () => {
    it("should_saveBeanstandeteWahlbriefe_when_called", async () => {
      const wahlbezirkID = "wahlbezirkId";
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const wahlID = "wahlID";
      const wvzNr = 1;

      const wahl = prepareWahl()
        .wahlID(wahlID)
        .waehlerverzeichnisNummer(wvzNr)
        .beanstandeteWahlbriefe(["ZUGELASSEN"])
        .build();

      const mockedWahlenGroupedByWvzNr = new Map<number, Wahl[]>([
        [wvzNr, [wahl]],
      ]);

      unitUnderTest.wahlenState.wahlen = [wahl];
      expect(unitUnderTest.wahlenState.wahlen).toBeDefined();

      mockDefinitions.postBeanstandeteWahlbriefe.mockReturnValue(
        Promise.resolve()
      );

      await unitUnderTest.beanstandeteWahlbriefeActions.saveBeanstandeteWahlbriefe();

      expect(mockDefinitions.postBeanstandeteWahlbriefe).toHaveBeenCalledWith(
        mockedWahlenGroupedByWvzNr,
        wahlbezirkID
      );
    });
  });

  describe("isBeanstandeteWahlbriefeSaving", () => {
    it("should_setIsSavingValue_when_saveBeanstandeteWahlbriefeCalled", async () => {
      const wahlbezirkID = "wahlbezirkId";
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const wahlID = "wahlID";
      const wvzNr = 1;

      unitUnderTest.wahlenState.wahlen = [
        prepareWahl()
          .wahlID(wahlID)
          .waehlerverzeichnisNummer(wvzNr)
          .beanstandeteWahlbriefe([])
          .build(),
      ];

      expect(
        unitUnderTest.beanstandeteWahlbriefeState.isBeanstandeteWahlbriefeSaving
      ).toBe(false);

      const promise =
        unitUnderTest.beanstandeteWahlbriefeActions.saveBeanstandeteWahlbriefe();

      expect(
        unitUnderTest.beanstandeteWahlbriefeState.isBeanstandeteWahlbriefeSaving
      ).toBe(true);

      const timeout = 100;
      vi.advanceTimersByTime(timeout);
      await promise;

      expect(
        unitUnderTest.beanstandeteWahlbriefeState.isBeanstandeteWahlbriefeSaving
      ).toBe(false);
    });
  });

  describe("summeGueltigerWahlbriefe", () => {
    it("should_calculateSummeGueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsEmpty", async () => {
      unitUnderTest.wahlenState.wahlen =
        _getWahlenWithoutBeanstandeteWahlbriefe();

      expect(
        unitUnderTest.beanstandeteWahlbriefeGetter.summeGueltigerWahlbriefe
      ).toStrictEqual([0]);
    });

    it("should_calculateSummeGueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsNotEmpty", async () => {
      unitUnderTest.wahlenState.wahlen = _getWahlenWithBeanstandeteWahlbriefe();

      expect(
        unitUnderTest.beanstandeteWahlbriefeGetter.summeGueltigerWahlbriefe
      ).toStrictEqual([1, 2]);
    });
  });

  describe("summeUngueltigerWahlbriefe", () => {
    it("should_calculateSummeUngueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsEmpty", async () => {
      unitUnderTest.wahlenState.wahlen =
        _getWahlenWithoutBeanstandeteWahlbriefe();

      expect(
        unitUnderTest.beanstandeteWahlbriefeGetter.summeUngueltigerWahlbriefe
      ).toStrictEqual([0]);
    });

    it("should_calculateSummeUngueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsNotEmpty", async () => {
      unitUnderTest.wahlenState.wahlen = _getWahlenWithBeanstandeteWahlbriefe();

      expect(
        unitUnderTest.beanstandeteWahlbriefeGetter.summeUngueltigerWahlbriefe
      ).toStrictEqual([0, 1]);
    });
  });

  describe("summenZurueckweisungsgruende", () => {
    it("should_calculateSummenZurueckweisungsgruende_when_BeanstandeteWahlbriefeIsEmpty", async () => {
      unitUnderTest.wahlenState.wahlen =
        _getWahlenWithoutBeanstandeteWahlbriefe();

      const summen =
        unitUnderTest.beanstandeteWahlbriefeGetter.summenZurueckweisungsgruende;
      summen.forEach((row) => {
        expect(row.summen).toStrictEqual([0]);
      });
    });

    it("should_calculateSummenZurueckweisungsgruende_when_BeanstandeteWahlbriefeIsNotEmpty", async () => {
      unitUnderTest.wahlenState.wahlen = _getWahlenWithBeanstandeteWahlbriefe();

      const summen =
        unitUnderTest.beanstandeteWahlbriefeGetter.summenZurueckweisungsgruende;
      summen.forEach((row) => {
        if (row.grund !== "GEGENSTAND_IM_UMSCHLAG") {
          expect(row.summen).toStrictEqual([0, 0]);
        } else {
          expect(row.summen).toStrictEqual([0, 1]);
        }
      });
    });

    it("should_ignoreBeanstandeWahlbriefeWithoutZurueckweisungsgrund_when_calculatingSum", () => {
      unitUnderTest.wahlenState.wahlen = [
        prepareWahl()
          .beanstandeteWahlbriefe([
            ZurueckweisungsgrundEnum.LoseStimmzettel,
            ZurueckweisungsgrundEnum.LoseStimmzettel,
            null,
            null,
            ZurueckweisungsgrundEnum.ScheinUngueltig,
            ZurueckweisungsgrundEnum.LoseStimmzettel,
          ])
          .build(),
      ];

      const summen =
        unitUnderTest.beanstandeteWahlbriefeGetter.summenZurueckweisungsgruende;
      const summeLoseStimmzettel = summen.find(
        (summe) => summe.grund === ZurueckweisungsgrundEnum.LoseStimmzettel
      );
      expect(summeLoseStimmzettel?.summen).toStrictEqual([3]);
    });
  });

  describe("loadStimmzettelumschlaege", () => {
    it("should_loadStimmzettelumschlaege_when_calledWithCorrectWahlID", async () => {
      const wahlID = "wahlID";
      unitUnderTest.wahlenState.wahlen = [prepareWahl().wahlID(wahlID).build()];

      const mockedStimmzettelumschlaege = createStimmzettelumschlaege();
      mockDefinitions.getStimmzettelumschlaege.mockReturnValue(
        mockedStimmzettelumschlaege
      );

      await unitUnderTest.stimmzettelumschlaegeActions.loadStimmzettelumschlaege(
        wahlID
      );

      expect(unitUnderTest.wahlenState.wahlen[0].wahlID).toStrictEqual(wahlID);
      expect(
        unitUnderTest.wahlenState.wahlen[0].stimmzettelumschlaege
      ).toStrictEqual(mockedStimmzettelumschlaege);
    });

    it("should_loadButNotUpdateStimmzettelumschlaege_when_calledWithCorrectWahlIdAndServiceReturned204", async () => {
      const wahlID = "wahlID";
      unitUnderTest.wahlenState.wahlen = [
        prepareWahl()
          .wahlID(wahlID)
          .stimmzettelumschlaege({ anzahlWaehler: null })
          .build(),
      ];

      mockDefinitions.getStimmzettelumschlaege.mockReturnValue(null);

      await unitUnderTest.stimmzettelumschlaegeActions.loadStimmzettelumschlaege(
        wahlID
      );

      expect(unitUnderTest.wahlenState.wahlen[0].wahlID).toStrictEqual(wahlID);
      expect(
        unitUnderTest.wahlenState.wahlen[0].stimmzettelumschlaege
      ).toStrictEqual({ anzahlWaehler: null });
    });
  });
});

function _getWahlenWithoutBeanstandeteWahlbriefe() {
  return [prepareWahl().beanstandeteWahlbriefe([]).build()];
}

function _getWahlenWithBeanstandeteWahlbriefe() {
  return [
    prepareWahl().beanstandeteWahlbriefe(["ZUGELASSEN"]).build(),
    prepareWahl()
      .beanstandeteWahlbriefe([
        "ZUGELASSEN",
        "GEGENSTAND_IM_UMSCHLAG",
        "ZUGELASSEN",
      ])
      .build(),
  ];
}
