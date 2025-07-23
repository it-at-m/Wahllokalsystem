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
    it("should_returnWahl_when_calledWithWahlId", () => {
      const wahlOne = createWahl();
      const wahlTwo = createWahl();
      const wahlThree = createWahl();

      unitUnderTest.wahlen = [wahlOne, wahlTwo, wahlThree];

      const result = unitUnderTest.getWahlOrUndefinedById(wahlOne.wahlID);

      expect(result).toStrictEqual(wahlOne);
    });

    it("should_returnUndefined_when_calledWithWahlIdThatDoesNotExist", () => {
      const wahlOne = createWahl();

      unitUnderTest.wahlen = [wahlOne];

      const result = unitUnderTest.getWahlOrUndefinedById("invalid id");

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

      await unitUnderTest.initBeanstandeteWahlbriefe(wvzNr);

      expect(unitUnderTest.wahlen[0].beanstandeteWahlbriefe).toStrictEqual([
        "ZUGELASSEN",
      ]);
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
});
