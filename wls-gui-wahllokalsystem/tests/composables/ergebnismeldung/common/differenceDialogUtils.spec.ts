import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/common/begruendungTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { prepareWahl } = useWahlTestDataFactory();
const { prepareWahlscheine } = useWahlscheineTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();
const { prepareWahldaten, prepareStimmabgabevermerke } =
  useStimmabgabevermerkeTestDataFactory();
const { prepareBegruendung } = useBegruendungTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getWahlOrUndefinedById: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
  postBegruendung: vi.fn(),
}));

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

describe("differenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useDifferenceDialogUtils>;
  let wahlscheineStore: ReturnType<typeof useWahlscheineStore>;
  let stimmabgabevermerkeStore: ReturnType<typeof useStimmabgabevermerkeStore>;
  let userStore: ReturnType<typeof useUserStore>;

  const WAHL_ID = "wahlId";
  const DIALOG = {
    isVisible: false,
    wahlId: WAHL_ID,
    begruendung: "Begründung",
    isBegruendungValid: false,
    anzahlWahlscheineOrStimmabgabevermerke: 2,
    anzahlStimmzettel: 3,
  };

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useDifferenceDialogUtils(WAHL_ID);
    wahlscheineStore = useWahlscheineStore();
    stimmabgabevermerkeStore = useStimmabgabevermerkeStore();
    userStore = useUserStore();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe("isWahlscheineUnequalToStimmzettel", () => {
    it.each([
      [false, 1, null],
      [false, 2, 2],
      [true, 3, 4],
    ])(
      "should_return%s_when_anzahlWahlscheineIs%sAndAnzahlStimmzettelIs%s",
      (result, anzahlWahlscheine, anzahlStimmzettel) => {
        userStore.user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
        wahlscheineStore.wahlscheine = [
          prepareWahlscheine()
            .bezirkUndWahlID(prepareBezirkUndWahlID().wahlID(WAHL_ID).build())
            .stimmabgabevermerke(anzahlWahlscheine)
            .build(),
        ];

        mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
          prepareWahl()
            .wahlID(WAHL_ID)
            .stimmzettelumschlaege({ anzahlWaehler: anzahlStimmzettel })
            .build()
        );

        expect(
          unitUnderTest.isWahlscheineUnequalToStimmzettel.value
        ).toStrictEqual(result);
      }
    );

    it.each([
      [false, 1, null],
      [false, 2, 2],
      [true, 3, 4],
    ])(
      "should_return%s_when_anzahlStimmabgabevermerkeIs%sAndAnzahlStimmzettelIs%s",
      (result, anzahlStimmabgabevermerke, anzahlStimmzettel) => {
        userStore.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;
        stimmabgabevermerkeStore.stimmabgabevermerke = [
          prepareStimmabgabevermerke()
            .wahldaten([
              prepareWahldaten()
                .wahlID(WAHL_ID)
                .eingenommeneWahlscheine(
                  new Map([
                    [
                      StimmzettelStimmzettelartEnum.Klein,
                      anzahlStimmabgabevermerke,
                    ],
                  ])
                )
                .build(),
            ])
            .build(),
        ];

        mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
          prepareWahl()
            .wahlID(WAHL_ID)
            .stimmzettelumschlaege({ anzahlWaehler: anzahlStimmzettel })
            .build()
        );

        expect(
          unitUnderTest.isWahlscheineUnequalToStimmzettel.value
        ).toStrictEqual(result);
      }
    );
  });

  describe("getBegruendung", () => {
    it("should_returnBegruendung_when_wahlAndWahlbezirkIdExists", async () => {
      userStore.user.wahlMetaData = [
        { wahlbezirkID: "wahlbezirkId", wahlID: WAHL_ID, wahlnummer: "0" },
      ];
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().build()
      );
      mockDefinitions.getBegruendungStimmzettelumschlaege.mockReturnValue(
        prepareBegruendung().build()
      );
      const begruendung = await unitUnderTest.getBegruendung();
      expect(begruendung).not.toBeUndefined();
    });

    it("should_returnNoBegruendung_when_wahlNotExists", async () => {
      userStore.user.wahlMetaData = [
        { wahlbezirkID: "wahlbezirkId", wahlID: WAHL_ID, wahlnummer: "0" },
      ];
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(null);
      const begruendung = await unitUnderTest.getBegruendung();
      expect(begruendung).toBeUndefined();
    });

    it("should_returnNoBegruendung_when_wahlbezirkIdNotExists", async () => {
      userStore.user.wahlMetaData = [];
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl().build()
      );
      const begruendung = await unitUnderTest.getBegruendung();
      expect(begruendung).toBeUndefined();
    });
  });

  describe("saveBegruendung", () => {
    it("should_saveBegruendung_when_wahlbezirkIdExists", () => {
      userStore.user.wahlMetaData = [
        { wahlbezirkID: "wahlbezirkId", wahlID: WAHL_ID, wahlnummer: "0" },
      ];
      unitUnderTest.saveBegruendung(DIALOG);
      expect(mockDefinitions.postBegruendung).toHaveBeenCalled();
    });

    it("should_notSaveBegruendung_when_wahlbezirkIdNotExists", () => {
      userStore.user.wahlMetaData = [];
      unitUnderTest.saveBegruendung(DIALOG);
      expect(mockDefinitions.postBegruendung).not.toHaveBeenCalled();
    });
  });

  describe("updateValidationStateForBegruendung", () => {
    it("should_updateValidation_when_calledWithDifferentBegruendung", () => {
      expect(DIALOG.isBegruendungValid).toBe(false);
      unitUnderTest.updateValidationStateForBegruendung(DIALOG);
      expect(DIALOG.isBegruendungValid).toBe(true);
      DIALOG.begruendung = "";
      unitUnderTest.updateValidationStateForBegruendung(DIALOG);
      expect(DIALOG.isBegruendungValid).toBe(false);
    });
  });
});
