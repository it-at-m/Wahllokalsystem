import type { DifferenceBegruendung } from "@/types/ergebnismeldung/common/DifferenceBegruendung.ts";

import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useSingleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/singleDifferenceDialogUtils.ts";
import router from "@/plugins/router.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { prepareWahl } = useWahlTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  routerPush: vi.fn(),
  useDifferenceDialogUtils: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  saveStimmzettelumschlaege: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
  postBegruendung: vi.fn(),
  setStepDone: vi.fn(),
}));

router.push = mockDefinitions.routerPush;

vi.mock(
  "@/composables/ergebnismeldung/common/differenceDialogUtils.ts",
  () => ({
    useDifferenceDialogUtils: mockDefinitions.useDifferenceDialogUtils,
  })
);

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
    stimmzettelumschlaegeActions: {
      saveStimmzettelumschlaege: mockDefinitions.saveStimmzettelumschlaege,
    },
  }),
}));

vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({
    setStepDone: mockDefinitions.setStepDone,
  }),
}));

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getBegruendungStimmzettelumschlaege:
      mockDefinitions.getBegruendungStimmzettelumschlaege,
    postBegruendung: mockDefinitions.postBegruendung,
  }),
}));

describe("useSingleDifferenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useSingleDifferenceDialogUtils>;
  let userStore: ReturnType<typeof useUserStore>;

  const WAHL_ID = "wahlId";
  const WAHLBEZIRK_ID = "wahlbezirkId";
  const DIALOG = {
    isVisible: true,
    differenceBegruendung: {
      wahlId: WAHL_ID,
      begruendung: "Testgrund",
      isBegruendungValid: true,
      anzahlWahlscheineOrStimmabgabevermerke: 10,
      anzahlStimmzettel: 5,
    },
  };

  beforeEach(() => {
    setActivePinia(createPinia());
    mockDefinitions.useDifferenceDialogUtils.mockReturnValue({
      anzahlWahlscheineOrStimmabgabevermerke: ref(10),
      anzahlStimmzettel: ref(5),
      isWahlscheineUnequalToStimmzettel: ref(true),
      updateValidationStateForBegruendung: vi
        .fn()
        .mockImplementation((differenceBegruendung: DifferenceBegruendung) => {
          differenceBegruendung.isBegruendungValid = true;
        }),
    });
    unitUnderTest = useSingleDifferenceDialogUtils(WAHL_ID, WAHLBEZIRK_ID);
    userStore = useUserStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege", () => {
    it("should_setDialog_when_checkIsCalledAndWahlscheineUnequalStimmzettel", async () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().build()
      );
      mockDefinitions.getBegruendungStimmzettelumschlaege.mockReturnValue(
        Promise.resolve({ grund: "Testgrund" })
      );

      await unitUnderTest.checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege();

      expect(
        mockDefinitions.getBegruendungStimmzettelumschlaege
      ).toHaveBeenCalled();
      expect(unitUnderTest.dialog.value).toEqual(DIALOG);
    });

    it("should_callSaveStimmzettelumschlaege_when_checkIsCalledAndWahlscheineEqualStimmzettel", async () => {
      mockDefinitions.useDifferenceDialogUtils.mockReturnValueOnce({
        ...useDifferenceDialogUtils(WAHL_ID),
        isWahlscheineUnequalToStimmzettel: ref(false),
      });
      unitUnderTest = useSingleDifferenceDialogUtils(WAHL_ID, WAHLBEZIRK_ID);

      await unitUnderTest.checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege();

      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
    });
  });

  describe("saveBegruendungAndStimmzettelumschlaege", () => {
    it("should_saveBegruendungAndStimmzettel_when_saveIsCalled", async () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();
      unitUnderTest.dialog.value = DIALOG;

      await unitUnderTest.saveBegruendungAndStimmzettelumschlaege();

      expect(unitUnderTest.dialog.value.isVisible).toBe(false);
      expect(mockDefinitions.postBegruendung).toHaveBeenCalled();
      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
      expect(mockDefinitions.routerPush).toHaveBeenCalled();
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        WAHL_ID,
        WAHLBEZIRK_ID,
        MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
      );
    });

    it("should_saveStimmzettel_when_saveIsCalledAndNoDialogExists", async () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      await unitUnderTest.saveBegruendungAndStimmzettelumschlaege();

      expect(mockDefinitions.postBegruendung).not.toHaveBeenCalled();
      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
      expect(mockDefinitions.routerPush).toHaveBeenCalled();
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        WAHL_ID,
        WAHLBEZIRK_ID,
        MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
      );
    });
  });

  describe("getDialogContent", () => {
    it("should_createDialogContent_when_isUWB", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().build()
      );

      unitUnderTest.dialog.value = DIALOG;
      const expected =
        "Die Anzahl der Stimmabgabevermerke (10) unterscheidet sich um 5 von der Anzahl der Stimmzettel (5)";
      expect(unitUnderTest.getDialogContent()).toBe(expected);
    });

    it("should_createDialogContent_when_isBWB", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().wahlID("xxx").build()
      );

      unitUnderTest.dialog.value = DIALOG;
      const expected =
        "Die Anzahl der Wahlscheine (10) unterscheidet sich um 5 von der Anzahl der Stimmzettel (5)";
      expect(unitUnderTest.getDialogContent()).toBe(expected);
    });

    it("should_createDialogContent_when_isBWBAndHauptwahl", () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .wahlMetaData([
          {
            wahlbezirkID: WAHLBEZIRK_ID,
            wahlID: WAHL_ID,
            wahlnummer: "0",
          },
        ])
        .build();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().wahlID(WAHL_ID).build()
      );

      unitUnderTest.dialog.value = DIALOG;
      const expected =
        "Die Anzahl der Wahlscheine (10) unterscheidet sich um 5 von der Anzahl der Stimmzettelumschläge (5)";
      expect(unitUnderTest.getDialogContent()).toBe(expected);
    });
  });
});
