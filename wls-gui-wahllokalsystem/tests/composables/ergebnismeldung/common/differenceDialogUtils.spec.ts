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

  describe("anzahlWahlscheineOrStimmabgabevermerke", () => {
    it("should_returnStimmabgabevermerke_when_isUWBAndStimmabgabevermerkeForWahlIdExists", () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;

      const eingenommeneWahlscheineAnzahl = 5;
      const testStimmabgabevermerke = prepareStimmabgabevermerke()
        .wahldaten([
          prepareWahldaten()
            .wahlID(WAHL_ID)
            .eingenommeneWahlscheine(
              new Map([[StimmzettelStimmzettelartEnum.Klein, eingenommeneWahlscheineAnzahl]])
            )
            .build(),
        ])
        .build();

      stimmabgabevermerkeStore.stimmabgabevermerke = [testStimmabgabevermerke];

      // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
      const vermerkeSum = testStimmabgabevermerke.wahldaten[0].vermerke.reduce(
        (sum, vermerk) => {
          const kleinStimmzettel = vermerk.stimmzettel.find(
            (s) => s.stimmzettelart === StimmzettelStimmzettelartEnum.Klein
          );
          return sum + (kleinStimmzettel?.anzahl ?? 0);
        },
        0
      );
      const expectedTotal = eingenommeneWahlscheineAnzahl + vermerkeSum;

      expect(
        unitUnderTest.anzahlWahlscheineOrStimmabgabevermerke.value
      ).toStrictEqual(expectedTotal);
    });

    it("should_returnUndefined_when_isUWBAndStimmabgabevermerkeForWahlIdNotExists", () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;
      stimmabgabevermerkeStore.stimmabgabevermerke = [];
      expect(
        unitUnderTest.anzahlWahlscheineOrStimmabgabevermerke.value
      ).toBeUndefined();
    });

    it("should_returnWahlscheine_when_isBWBAndWahlscheinForWahlIdExists", () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
      wahlscheineStore.wahlscheine = [
        prepareWahlscheine()
          .bezirkUndWahlID(prepareBezirkUndWahlID().wahlID(WAHL_ID).build())
          .stimmabgabevermerke(5)
          .build(),
      ];
      expect(
        unitUnderTest.anzahlWahlscheineOrStimmabgabevermerke.value
      ).toStrictEqual(5);
    });

    it("should_returnUndefined_when_isBWBAndWahlscheinForWahlIdNotExists", () => {
      userStore.user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
      wahlscheineStore.wahlscheine = [];
      expect(
        unitUnderTest.anzahlWahlscheineOrStimmabgabevermerke.value
      ).toBeUndefined();
    });
  });

  describe("anzahlStimmzettel", () => {
    it("should_returnStimmzettelumschlaegeAnzahlWaehler_when_exists", () => {
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
        prepareWahl()
          .wahlID(WAHL_ID)
          .stimmzettelumschlaege({ anzahlWaehler: 2 })
          .build()
      );
      expect(unitUnderTest.anzahlStimmzettel.value).toStrictEqual(2);
    });

    it("should_returnUndefined_when_notExists", () => {
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(null);
      expect(unitUnderTest.anzahlStimmzettel.value).toBeUndefined();
    });
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
      (result, eingenommeneWahlscheineAnzahl, anzahlStimmzettel) => {
        userStore.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;

        const testStimmabgabevermerke = prepareStimmabgabevermerke()
          .wahldaten([
            prepareWahldaten()
              .wahlID(WAHL_ID)
              .eingenommeneWahlscheine(
                new Map([
                  [
                    StimmzettelStimmzettelartEnum.Klein,
                    eingenommeneWahlscheineAnzahl,
                  ],
                ])
              )
              .build(),
          ])
          .build();

        stimmabgabevermerkeStore.stimmabgabevermerke = [
          testStimmabgabevermerke,
        ];

        const vermerkeSum =
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          testStimmabgabevermerke.wahldaten[0].vermerke.reduce(
            (sum, vermerk) => {
              const kleinStimmzettel = vermerk.stimmzettel.find(
                (s) => s.stimmzettelart === StimmzettelStimmzettelartEnum.Klein
              );
              return sum + (kleinStimmzettel?.anzahl ?? 0);
            },
            0
          );

        const totalStimmabgabevermerke =
          eingenommeneWahlscheineAnzahl + vermerkeSum;

        mockDefinitions.getWahlOrUndefinedById.mockReturnValue(
          prepareWahl()
            .wahlID(WAHL_ID)
            .stimmzettelumschlaege({ anzahlWaehler: anzahlStimmzettel })
            .build()
        );

        const expectedResult =
          anzahlStimmzettel != null &&
          totalStimmabgabevermerke !== anzahlStimmzettel;

        expect(
          unitUnderTest.isWahlscheineUnequalToStimmzettel.value
        ).toStrictEqual(expectedResult);
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
