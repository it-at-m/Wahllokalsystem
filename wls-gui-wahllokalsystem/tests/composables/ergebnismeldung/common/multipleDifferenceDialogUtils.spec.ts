import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useMultipleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/multipleDifferenceDialogUtils.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { prepareWahlscheine } = useWahlscheineTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();
const { prepareWahldaten, prepareStimmabgabevermerke } =
  useStimmabgabevermerkeTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  useDifferenceDialogUtils: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnismeldung/common/differenceDialogUtils.ts",
  () => ({
    useDifferenceDialogUtils: mockDefinitions.useDifferenceDialogUtils,
  })
);

describe("useMultipleDifferenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useMultipleDifferenceDialogUtils>;
  let userStore: ReturnType<typeof useUserStore>;
  let stimmabgabevermerkeStore: ReturnType<typeof useStimmabgabevermerkeStore>;
  let wahlscheineStore: ReturnType<typeof useWahlscheineStore>;

  const WAHL_ID = "wahlId";
  const DIALOG = {
    isVisible: true,
    wahlId: WAHL_ID,
    begruendung: "Begründung",
    isBegruendungValid: false,
    anzahlWahlscheineOrStimmabgabevermerke: 2,
    anzahlStimmzettel: 3,
  };

  const mockUpdateValidationStateForBegruendung = vi.fn();
  const mockSaveBegruendung = vi.fn();
  const mockGetBegruendung = vi.fn(() =>
    Promise.resolve({ grund: "Testgrund" })
  );

  beforeEach(() => {
    setActivePinia(createPinia());
    mockDefinitions.useDifferenceDialogUtils.mockReturnValue({
      anzahlWahlscheineOrStimmabgabevermerke: ref(10),
      anzahlStimmzettel: ref(5),
      isWahlscheineUnequalToStimmzettel: ref(true),
      getBegruendung: mockGetBegruendung,
      saveBegruendung: mockSaveBegruendung,
      updateValidationStateForBegruendung:
        mockUpdateValidationStateForBegruendung,
    });
    unitUnderTest = useMultipleDifferenceDialogUtils();
    userStore = useUserStore();
    stimmabgabevermerkeStore = useStimmabgabevermerkeStore();
    wahlscheineStore = useWahlscheineStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("onSaveClicked", () => {
    it("should_checkForDifferenceInStimmabgabevermerke_when_isUWBWithOneWahl", async () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;
      stimmabgabevermerkeStore.stimmabgabevermerke = [
        prepareStimmabgabevermerke()
          .wahldaten([
            prepareWahldaten()
              .wahlID(WAHL_ID)
              .eingenommeneWahlscheine(
                new Map([[StimmzettelStimmzettelartEnum.Klein, 3]])
              )
              .build(),
          ])
          .build(),
      ];
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(0);

      await unitUnderTest.onSaveClicked();

      expect(mockGetBegruendung).toHaveBeenCalled();
      expect(mockUpdateValidationStateForBegruendung).toHaveBeenCalled();
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(1);
    });

    it("should_checkForDifferenceInWahlscheine_when_isBWBWithOneWahl", async () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
      wahlscheineStore.wahlscheine = [
        prepareWahlscheine()
          .bezirkUndWahlID(prepareBezirkUndWahlID().wahlID(WAHL_ID).build())
          .stimmabgabevermerke(1)
          .build(),
      ];
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(0);

      await unitUnderTest.onSaveClicked();

      expect(mockGetBegruendung).toHaveBeenCalled();
      expect(mockUpdateValidationStateForBegruendung).toHaveBeenCalled();
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(1);
    });

    it("should_checkForDifferenceInWahlscheine_when_isBWBWithMultipleWahl", async () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
      wahlscheineStore.wahlscheine = [
        prepareWahlscheine()
          .bezirkUndWahlID(prepareBezirkUndWahlID().wahlID(WAHL_ID).build())
          .stimmabgabevermerke(1)
          .build(),
        prepareWahlscheine()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(WAHL_ID + "2")
              .build()
          )
          .stimmabgabevermerke(1)
          .build(),
      ];
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(0);

      await unitUnderTest.onSaveClicked();

      expect(mockGetBegruendung).toHaveBeenCalledTimes(2);
      expect(mockUpdateValidationStateForBegruendung).toHaveBeenCalledTimes(2);
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(2);
    });
  });

  describe("onConfirmClicked", () => {
    it("should_saveBegruendungAndCloseDialog_when_onConfirmClickedIsCalled", async () => {
      await unitUnderTest.onConfirmClicked(DIALOG);

      expect(mockSaveBegruendung).toHaveBeenCalled();
      expect(DIALOG.isVisible).toStrictEqual(false);
    });
  });
});
