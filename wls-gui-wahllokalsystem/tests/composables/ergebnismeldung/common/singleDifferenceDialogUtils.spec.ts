import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useSingleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/singleDifferenceDialogUtils.ts";

const mockDefinitions = vi.hoisted(() => ({
  useDifferenceDialogUtils: vi.fn(),
  saveStimmzettelumschlaege: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnismeldung/common/differenceDialogUtils.ts",
  () => ({
    useDifferenceDialogUtils: mockDefinitions.useDifferenceDialogUtils,
  })
);

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    stimmzettelumschlaegeActions: {
      saveStimmzettelumschlaege: mockDefinitions.saveStimmzettelumschlaege,
    },
  }),
}));

describe("useSingleDifferenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useSingleDifferenceDialogUtils>;

  const WAHL_ID = "wahlId";

  const mockUpdateValidationStateForBegruendung = vi.fn();
  const mockSaveBegruendung = vi.fn();
  const mockGetBegruendung = vi.fn(() =>
    Promise.resolve({ grund: "Testgrund" })
  );

  beforeEach(() => {
    mockDefinitions.useDifferenceDialogUtils.mockReturnValue({
      anzahlWahlscheineOrStimmabgabevermerke: ref(10),
      anzahlStimmzettel: ref(5),
      isWahlscheineUnequalToStimmzettel: ref(true),
      getBegruendung: mockGetBegruendung,
      saveBegruendung: mockSaveBegruendung,
      updateValidationStateForBegruendung:
        mockUpdateValidationStateForBegruendung,
    });
    unitUnderTest = useSingleDifferenceDialogUtils(WAHL_ID);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("onSaveClicked", () => {
    it("should_setDialog_when_onSaveClickedIsCalledAndWahlscheineUnequalStimmzettel", async () => {
      await unitUnderTest.onSaveClicked();

      expect(unitUnderTest.dialog.value).toEqual({
        isVisible: true,
        wahlId: WAHL_ID,
        begruendung: "Testgrund",
        isBegruendungValid: false,
        anzahlWahlscheineOrStimmabgabevermerke: 10,
        anzahlStimmzettel: 5,
      });
      expect(mockGetBegruendung).toHaveBeenCalled();
      expect(mockUpdateValidationStateForBegruendung).toHaveBeenCalledWith(
        unitUnderTest.dialog.value
      );
    });

    it("should_callSaveStimmzettelumschlaege_when_onSaveClickedIsCalledAndWahlscheineEqualStimmzettel", async () => {
      mockDefinitions.useDifferenceDialogUtils.mockReturnValueOnce({
        ...useDifferenceDialogUtils(),
        isWahlscheineUnequalToStimmzettel: ref(false),
      });
      unitUnderTest = useSingleDifferenceDialogUtils(WAHL_ID);

      await unitUnderTest.onSaveClicked();

      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
    });
  });

  describe("onConfirmClicked", () => {
    it("should_saveBegruendungAndStimmzettel_when_onConfirmClickedIsCalled", async () => {
      unitUnderTest.dialog.value = {
        isVisible: true,
        wahlId: WAHL_ID,
        begruendung: "Testgrund",
        isBegruendungValid: false,
        anzahlWahlscheineOrStimmabgabevermerke: 10,
        anzahlStimmzettel: 5,
      };

      await unitUnderTest.onConfirmClicked();

      expect(unitUnderTest.dialog.value.isVisible).toBe(false);
      expect(mockSaveBegruendung).toHaveBeenCalledWith(
        unitUnderTest.dialog.value
      );
      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalledWith(
        WAHL_ID
      );
    });
  });
});
