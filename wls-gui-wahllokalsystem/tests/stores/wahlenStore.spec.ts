import type { Wahl } from "@/types/wahl/Wahl.ts";

import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
  postBeanstandeteWahlbriefe: vi.fn(),
  getBeanstandeteWahlbriefe: vi.fn(),
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

const { createWahl, prepareWahl } = useWahlTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareBeanstandeteWahlbriefe } =
  useBeanstandeteWahlbriefeTestDataFactory();

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
      userStore.setUser(prepareUser().wahltagID(wahltagID).build());

      const wahl1 = prepareWahl().nummer("dcba").build();
      const wahl2 = prepareWahl().nummer("abcd").build();
      const mockedWahlArrayFromService = [wahl1, wahl2];
      const expectedSortedWahlArray = [wahl2, wahl1];

      mockDefinitions.getWahlen.mockReturnValue(
        Promise.resolve(mockedWahlArrayFromService)
      );

      await unitUnderTest.initWahlen();

      expect(unitUnderTest.wahlen).toStrictEqual(expectedSortedWahlArray);
    });
  });

  describe("waehlerverzeichnisNummern", () => {
    it("should_returnEmptyList_when_wahlenDoNotExist", () => {
      expect(unitUnderTest.waehlerverzeichnisNummern).toStrictEqual([]);
    });

    it("should_returnListOfWvzNummern_when_wahlenExist", () => {
      unitUnderTest.wahlen = [
        prepareWahl().waehlerverzeichnisNummer(1).build(),
        prepareWahl().waehlerverzeichnisNummer(2).build(),
      ];

      expect(unitUnderTest.waehlerverzeichnisNummern).toStrictEqual([1, 2]);
    });
  });

  describe("getWaehlerverzeichnisOrUndefinedById", () => {
    it("should_returnWaehlerverzeichnisNummer_when_calledWithId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWaehlerverzeichnisOrUndefinedById(
        wahlTwo.wahlID
      );

      expect(result).toStrictEqual(wahlTwo.waehlerverzeichnisNummer);
    });

    it("should_returnUndefined_when_calledWithIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo];

      const result = unitUnderTest.getWaehlerverzeichnisOrUndefinedById(
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

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlNameOrBlankStringById(wahlOne.wahlID);

      expect(result).toStrictEqual(wahlOne.name);
    });
  });

  describe("getWahlTagOrBlankStringById", () => {
    it("should_getWahlTag_when_calledWithWahlId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlTagOrBlankStringById(wahlOne.wahlID);

      expect(result).toStrictEqual(wahlOne.wahltag);
    });

    it("should_getBlank_when_calledWithWahlIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();
      const wahlFour = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlTagOrBlankStringById(wahlFour.wahlID);

      expect(result).toStrictEqual("");
    });
  });

  describe("getWahlOrUndefinedById", () => {
    it("should_getWahl_when_calledWithWahlId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlOrUndefinedById(wahlOne.wahlID);

      expect(result).toStrictEqual(wahlOne);
    });

    it("should_getUndefined_when_calledWithWahlIdThatDoesNotExist", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();
      const wahlFour = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlOrUndefinedById(wahlFour.wahlID);

      expect(result).toBeUndefined();
    });

    it("should_getUndefined_when_wahlenDoNotExist", () => {
      unitUnderTest.wahlen = null;

      const result = unitUnderTest.getWahlOrUndefinedById("testId");

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

      unitUnderTest.wahlen = [
        prepareWahl()
          .wahlID(wahlID)
          .waehlerverzeichnisNummer(wvzNr)
          .beanstandeteWahlbriefe([])
          .build(),
      ];

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([]);

      await unitUnderTest.initBeanstandeteWahlbriefe();

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([
        "ZUGELASSEN",
      ]);
    });

    it("should_notCallService_when_noWahlenGiven", async () => {
      unitUnderTest.wahlen = [];

      await unitUnderTest.initBeanstandeteWahlbriefe();

      expect(mockDefinitions.getBeanstandeteWahlbriefe).not.toHaveBeenCalled();
    });
  });

  describe("addBeanstandeterWahlbriefEntry", () => {
    it("should_addBeanstandeteWahlbriefeEntryInStore_when_addWahlbriefIsCalled", () => {
      unitUnderTest.wahlen = [
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

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
      ]);
      expect(unitUnderTest.wahlen[1].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
      ]);

      unitUnderTest.addBeanstandeterWahlbriefEntry();

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
        null,
      ]);
      expect(unitUnderTest.wahlen[1].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
        null,
      ]);
    });
  });

  describe("deleteBeanstandeterWahlbriefEntry", () => {
    it("should_deleteBeanstandeterWahlbriefEntryFromWahlen_when_called", () => {
      unitUnderTest.wahlen = [
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

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
        "ZUGELASSEN",
      ]);
      expect(unitUnderTest.wahlen[1].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
        "ZUGELASSEN",
      ]);

      unitUnderTest.deleteBeanstandeterWahlbriefEntry(1);

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
      ]);
      expect(unitUnderTest.wahlen[1].beanstandeteWahlbriefe).toStrictEqual([
        "GEGENSTAND_IM_UMSCHLAG",
      ]);
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

      unitUnderTest.wahlen = [wahl];
      expect(unitUnderTest.wahlen).toBeDefined();

      mockDefinitions.postBeanstandeteWahlbriefe.mockReturnValue(
        Promise.resolve()
      );

      await unitUnderTest.saveBeanstandeteWahlbriefe();

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

      unitUnderTest.wahlen = [
        prepareWahl()
          .wahlID(wahlID)
          .waehlerverzeichnisNummer(wvzNr)
          .beanstandeteWahlbriefe([])
          .build(),
      ];

      expect(unitUnderTest.isBeanstandeteWahlbriefeSaving).toBe(false);

      const promise = unitUnderTest.saveBeanstandeteWahlbriefe();

      expect(unitUnderTest.isBeanstandeteWahlbriefeSaving).toBe(true);

      const timeout = 100;
      vi.advanceTimersByTime(timeout);
      await promise;

      expect(unitUnderTest.isBeanstandeteWahlbriefeSaving).toBe(false);
    });
  });

  describe("summeGueltigerWahlbriefe", () => {
    it("should_calculateSummeGueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsEmpty", async () => {
      unitUnderTest.wahlen = _getWahlenWithoutBeanstandeteWahlbriefe();

      expect(unitUnderTest.summeGueltigerWahlbriefe).toStrictEqual([0]);
    });

    it("should_calculateSummeGueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsNotEmpty", async () => {
      unitUnderTest.wahlen = _getWahlenWithBeanstandeteWahlbriefe();

      expect(unitUnderTest.summeGueltigerWahlbriefe).toStrictEqual([1, 2]);
    });
  });

  describe("summeUngueltigerWahlbriefe", () => {
    it("should_calculateSummeUngueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsEmpty", async () => {
      unitUnderTest.wahlen = _getWahlenWithoutBeanstandeteWahlbriefe();

      expect(unitUnderTest.summeUngueltigerWahlbriefe).toStrictEqual([0]);
    });

    it("should_calculateSummeUngueltigerWahlbriefe_when_BeanstandeteWahlbriefeIsNotEmpty", async () => {
      unitUnderTest.wahlen = _getWahlenWithBeanstandeteWahlbriefe();

      expect(unitUnderTest.summeUngueltigerWahlbriefe).toStrictEqual([0, 1]);
    });
  });

  describe("summenZurueckweisungsgruende", () => {
    it("should_calculateSummenZurueckweisungsgruende_when_BeanstandeteWahlbriefeIsEmpty", async () => {
      unitUnderTest.wahlen = _getWahlenWithoutBeanstandeteWahlbriefe();

      unitUnderTest.summenZurueckweisungsgruende.forEach((row) => {
        expect(row.summen).toStrictEqual([0]);
      });
    });

    it("should_calculateSummenZurueckweisungsgruende_when_BeanstandeteWahlbriefeIsNotEmpty", async () => {
      unitUnderTest.wahlen = _getWahlenWithBeanstandeteWahlbriefe();

      unitUnderTest.summenZurueckweisungsgruende.forEach((row) => {
        if (row.grund !== "GEGENSTAND_IM_UMSCHLAG") {
          expect(row.summen).toStrictEqual([0, 0]);
        } else {
          expect(row.summen).toStrictEqual([0, 1]);
        }
      });
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
