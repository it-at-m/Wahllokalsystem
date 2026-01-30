import type { DifferenceBegruendung } from "@/types/ergebnismeldung/common/DifferenceBegruendung.ts";

import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useSingleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/singleDifferenceDialogUtils.ts";

const { prepareWahl } = useWahlTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  useDifferenceDialogUtils: vi.fn(),
  getWahlbezirkIdFromWahlMetaDataByWahlId: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  saveStimmzettelumschlaege: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
  postBegruendung: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnismeldung/common/differenceDialogUtils.ts",
  () => ({
    useDifferenceDialogUtils: mockDefinitions.useDifferenceDialogUtils,
  })
);

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    getWahlbezirkIdFromWahlMetaDataByWahlId:
      mockDefinitions.getWahlbezirkIdFromWahlMetaDataByWahlId,
  }),
}));

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

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getBegruendungStimmzettelumschlaege:
      mockDefinitions.getBegruendungStimmzettelumschlaege,
    postBegruendung: mockDefinitions.postBegruendung,
  }),
}));

describe("useSingleDifferenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useSingleDifferenceDialogUtils>;

  const WAHL_ID = "wahlId";
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
    unitUnderTest = useSingleDifferenceDialogUtils(WAHL_ID);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege", () => {
    it("should_setDialog_when_onSaveClickedIsCalledAndWahlscheineUnequalStimmzettel", async () => {
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().build()
      );
      mockDefinitions.getWahlbezirkIdFromWahlMetaDataByWahlId.mockReturnValue(
        "wahlbezirkId"
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

    it("should_callSaveStimmzettelumschlaege_when_onSaveClickedIsCalledAndWahlscheineEqualStimmzettel", async () => {
      mockDefinitions.useDifferenceDialogUtils.mockReturnValueOnce({
        ...useDifferenceDialogUtils(WAHL_ID),
        isWahlscheineUnequalToStimmzettel: ref(false),
      });
      unitUnderTest = useSingleDifferenceDialogUtils(WAHL_ID);

      await unitUnderTest.checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege();

      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
    });
  });

  describe("saveBegruendungAndStimmzettelumschlaege", () => {
    it("should_saveBegruendungAndStimmzettel_when_onConfirmClickedIsCalled", async () => {
      unitUnderTest.dialog.value = DIALOG;

      await unitUnderTest.saveBegruendungAndStimmzettelumschlaege();

      expect(unitUnderTest.dialog.value.isVisible).toBe(false);
      expect(mockDefinitions.postBegruendung).toHaveBeenCalled();
      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
    });
  });
});
