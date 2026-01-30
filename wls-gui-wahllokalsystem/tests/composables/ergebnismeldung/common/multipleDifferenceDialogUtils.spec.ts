import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
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
const { prepareWahl } = useWahlTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  useDifferenceDialogUtils: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
  postBegruendung: vi.fn(),
  postStimmabgabevermerke: vi.fn(),
  postWahlscheine: vi.fn(),
}));

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
  }),
}));

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getBegruendungStimmzettelumschlaege:
      mockDefinitions.getBegruendungStimmzettelumschlaege,
    postBegruendung: mockDefinitions.postBegruendung,
  }),
}));

vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      postStimmabgabevermerke: mockDefinitions.postStimmabgabevermerke,
    }),
  })
);

vi.mock("@/composables/ergebnismeldung/common/wahlscheineService.ts", () => ({
  useWahlscheineService: () => ({
    postWahlscheine: mockDefinitions.postWahlscheine,
  }),
}));

describe("useMultipleDifferenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useMultipleDifferenceDialogUtils>;
  let userStore: ReturnType<typeof useUserStore>;
  let stimmabgabevermerkeStore: ReturnType<typeof useStimmabgabevermerkeStore>;
  let wahlscheineStore: ReturnType<typeof useWahlscheineStore>;

  const WAHL_ID = "wahlId";
  const WAHLBEZIRK_ID = "wahlbezirkId";
  const DIALOG = {
    isVisible: true,
    differenceBegruendung: {
      wahlId: WAHL_ID,
      begruendung: "Testgrund",
      isBegruendungValid: true,
      anzahlWahlscheineOrStimmabgabevermerke: 2,
      anzahlStimmzettel: 3,
    },
  };

  beforeEach(() => {
    setActivePinia(createPinia());
    mockDefinitions.useDifferenceDialogUtils.mockReturnValue({
      anzahlWahlscheineOrStimmabgabevermerke: ref(10),
      anzahlStimmzettel: ref(5),
      isWahlscheineUnequalToStimmzettel: ref(true),
    });
    unitUnderTest = useMultipleDifferenceDialogUtils();
    userStore = useUserStore();
    stimmabgabevermerkeStore = useStimmabgabevermerkeStore();
    wahlscheineStore = useWahlscheineStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine", () => {
    it.each([[_setupUWB], [_setupBWB]])(
      "should_checkForDifference_when_thereIsOneWahl",
      async (setupFunction) => {
        setupFunction();
        mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
          prepareWahl().build()
        );
        mockDefinitions.getBegruendungStimmzettelumschlaege.mockReturnValue(
          Promise.resolve({ grund: "Testgrund" })
        );

        expect(unitUnderTest.dialogs.value.length).toStrictEqual(0);

        await unitUnderTest.checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine();

        expect(
          mockDefinitions.getBegruendungStimmzettelumschlaege
        ).toHaveBeenCalled();
        expect(unitUnderTest.dialogs.value.length).toStrictEqual(1);
      }
    );

    it("should_checkForDifferenceInWahlscheine_when_isBWBWithMultipleWahl", async () => {
      userStore.user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .wahlMetaData([
          {
            wahlbezirkID: WAHLBEZIRK_ID,
            wahlID: WAHL_ID,
            wahlnummer: "0",
          },
          {
            wahlbezirkID: WAHLBEZIRK_ID + "2",
            wahlID: WAHL_ID + "2",
            wahlnummer: "1",
          },
        ])
        .build();
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
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().build()
      );
      mockDefinitions.getBegruendungStimmzettelumschlaege.mockReturnValue(
        Promise.resolve({ grund: "Testgrund" })
      );

      expect(unitUnderTest.dialogs.value.length).toStrictEqual(0);

      await unitUnderTest.checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine();

      expect(
        mockDefinitions.getBegruendungStimmzettelumschlaege
      ).toHaveBeenCalledTimes(2);
      expect(unitUnderTest.dialogs.value.length).toStrictEqual(2);
    });
  });

  describe("saveBegruendungAndStimmabgabevermerkeWahlscheine", () => {
    it("should_saveStimmabgabevermerkeAndBegruendungAndCloseDialog_when_onConfirmClickedIsCalledInUWB", async () => {
      _setupUWB();
      unitUnderTest.dialogs.value = [DIALOG];

      await unitUnderTest.saveBegruendungAndStimmabgabevermerkeWahlscheine(
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        unitUnderTest.dialogs.value[0]!
      );

      expect(mockDefinitions.postBegruendung).toHaveBeenCalled();
      expect(DIALOG.isVisible).toStrictEqual(false);
      expect(mockDefinitions.postStimmabgabevermerke).toHaveBeenCalled();
    });

    it("should_saveWahlscheineAndBegruendungAndCloseDialog_when_onConfirmClickedIsCalledInBWB", async () => {
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
      wahlscheineStore.wahlscheine = [
        prepareWahlscheine()
          .bezirkUndWahlID(prepareBezirkUndWahlID().wahlID(WAHL_ID).build())
          .stimmabgabevermerke(1)
          .build(),
      ];
      unitUnderTest.dialogs.value = [DIALOG];

      await unitUnderTest.saveBegruendungAndStimmabgabevermerkeWahlscheine(
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        unitUnderTest.dialogs.value[0]!
      );

      expect(mockDefinitions.postBegruendung).toHaveBeenCalled();
      expect(DIALOG.isVisible).toStrictEqual(false);
      expect(mockDefinitions.postWahlscheine).toHaveBeenCalled();
    });
  });

  describe("updateValidationStateForBegruendung", () => {
    it("should_updateValidation_when_calledWithDifferentBegruendung", () => {
      DIALOG.differenceBegruendung.begruendung = "Testgrund";
      unitUnderTest.updateValidationStateForBegruendung(
        DIALOG.differenceBegruendung
      );
      expect(DIALOG.differenceBegruendung.isBegruendungValid).toBe(true);
      DIALOG.differenceBegruendung.begruendung = "";
      unitUnderTest.updateValidationStateForBegruendung(
        DIALOG.differenceBegruendung
      );
      expect(DIALOG.differenceBegruendung.isBegruendungValid).toBe(false);
    });
  });

  function _setupUWB() {
    userStore.user = prepareUser()
      .wahlbezirksArt(WahlbezirksArtEnum.UWB)
      .wahlMetaData([
        {
          wahlbezirkID: WAHLBEZIRK_ID,
          wahlID: WAHL_ID,
          wahlnummer: "0",
        },
      ])
      .build();
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
  }

  function _setupBWB() {
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
    wahlscheineStore.wahlscheine = [
      prepareWahlscheine()
        .bezirkUndWahlID(prepareBezirkUndWahlID().wahlID(WAHL_ID).build())
        .stimmabgabevermerke(1)
        .build(),
    ];
  }
});
