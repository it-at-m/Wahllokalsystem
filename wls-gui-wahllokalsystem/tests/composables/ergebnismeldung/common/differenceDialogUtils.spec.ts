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

const mockDefinitions = vi.hoisted(() => ({
  getWahlOrUndefinedById: vi.fn(),
}));

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
  }),
}));

describe("differenceDialogUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useDifferenceDialogUtils>;
  let wahlscheineStore: ReturnType<typeof useWahlscheineStore>;
  let stimmabgabevermerkeStore: ReturnType<typeof useStimmabgabevermerkeStore>;
  let userStore: ReturnType<typeof useUserStore>;

  const WAHL_ID = "wahlId";
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
});
