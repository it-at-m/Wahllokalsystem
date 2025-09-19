import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomNumber, getRandomItem } = useCommonTestDataFactory();
const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag, prepareWahlvorschlaege } =
  useWahlvorschlaegeTestDataFactory();

type ErgebnisWithErgebnis = Ergebnis & { ergebnis: number };
type ErgebnisWithNumIndex = Ergebnis & { numIndex: number };

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisseByWahlIdAndStapelartOrUndefined: vi.fn(),
  getWahlvorschlaegeByWahlIDAndWahlbezirkID: vi.fn(),
  switchStapelOfErgebnis: vi.fn(),
}));

vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: () => ({
    getErgebnisseByWahlIdAndStapelartOrUndefined:
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined,
    switchStapelOfErgebnis: mockDefinitions.switchStapelOfErgebnis,
  }),
}));
vi.mock("@/stores/wahlvorschlaegeStore.ts", () => ({
  useWahlvorschlaegeStore: () => ({
    getWahlvorschlaegeByWahlIDAndWahlbezirkID:
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID,
  }),
}));

describe("obwStapelCUtils", () => {
  let unitUnderTest: ReturnType<typeof useOBWStapelCUtils>;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  function createMockImplementationForGetErgebnisseByWahlIdAndStapelartOrUndefinedWithErgebnisseForStapelArt(
    ergebnisse: Map<StapelArtEnum, Ergebnisse>
  ) {
    return (wahlID: string, stapelArt: StapelArtEnum) => {
      return ergebnisse.get(stapelArt);
    };
  }

  function initUnitUnderTest() {
    unitUnderTest = useOBWStapelCUtils(
      computed(() => wahlID),
      computed(() => wahlbezirkID)
    );
  }

  beforeEach(() => {
    initUnitUnderTest();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe("stapelCUngueltigErgebnisse", () => {
    it("should_returnArrayOfErgebnisse_when_ergebnisseForWahlIDAndStapelObwCUngueltigAreGiven", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisse.value;

      const expectedResult: ErgebnisAndStapelArt[] = [
        { ergebnis: ergebnis1, stapelArt: StapelArtEnum.ObwCUngueltig },
        { ergebnis: ergebnis2, stapelArt: StapelArtEnum.ObwCUngueltig },
      ];
      expect(result).toStrictEqual(expectedResult);
      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mock.calls
      ).toStrictEqual([[wahlID, StapelArtEnum.ObwCUngueltig]]);
    });

    it("should_returnEmptyArray_when_noErgebnisseForWahlIDAndStapelObwCUngueltigAreGiven", () => {
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        undefined
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisse.value;
      expect(result).toStrictEqual([]);
    });
  });

  describe("stapelCUngueltigErgebnisseSum", () => {
    it("should_returnSumOfUngueltigValues_when_valuesAreGiven", async () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mock.calls
      ).toStrictEqual([[wahlID, StapelArtEnum.ObwCUngueltig]]);
      expect(result).toStrictEqual(ergebnis1.ergebnis + ergebnis2.ergebnis);
    });

    it("should_returnZero_when_ergebnisseStapelCUngueltigIsEmptyArray", () => {
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse().ergebnisse([]).build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(result).toStrictEqual(0);
    });

    it("should_countNullAsZero_when_valuesAreGiven", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis().ergebnis(null).build();
      const ergebnis3 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse()
          .ergebnisse([ergebnis1, ergebnis2, ergebnis3])
          .build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(result).toStrictEqual(ergebnis1.ergebnis + ergebnis3.ergebnis);
    });
  });

  describe("stapelCGueltigErgebnisse", () => {
    it("should_returnArrayOfErgebnisse_when_ergebnisseForWahlIDAndStapelObwCUngueltigAreGiven", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result = unitUnderTest.stapelCGueltigErgebnisse.value;

      const expectedResult: ErgebnisAndStapelArt[] = [
        { ergebnis: ergebnis1, stapelArt: StapelArtEnum.ObwCGueltig },
        { ergebnis: ergebnis2, stapelArt: StapelArtEnum.ObwCGueltig },
      ];
      expect(result).toStrictEqual(expectedResult);
      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mock.calls
      ).toStrictEqual([[wahlID, StapelArtEnum.ObwCGueltig]]);
    });

    it("should_returnEmptyArray_when_noErgebnisseForWahlIDAndStapelObwCUngueltigAreGiven", () => {
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        undefined
      );

      const result = unitUnderTest.stapelCGueltigErgebnisse.value;
      expect(result).toStrictEqual([]);
    });
  });

  describe("stapelCGueltigErgebnisseSums", () => {
    it("should_groupAndSumByWahlvorschlagID_when_ergebnisseStapelCGueltigAreGiven", () => {
      const ergebnis1Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis3Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis1Wahlvorschlag2 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag2")
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2Wahlvorschlag2 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag2")
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis1Wahlvorschlag3 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag3")
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse()
          .ergebnisse([
            ergebnis1Wahlvorschlag1,
            ergebnis2Wahlvorschlag1,
            ergebnis1Wahlvorschlag2,
            ergebnis1Wahlvorschlag3,
            ergebnis2Wahlvorschlag2,
            ergebnis3Wahlvorschlag1,
          ])
          .build()
      );

      const result = unitUnderTest.stapelCGueltigErgebnisseSums.value;

      expect(result.size).toStrictEqual(3);
      expect(result.get("wahlvorschlag1")).toStrictEqual(
        ergebnis1Wahlvorschlag1.ergebnis +
          ergebnis2Wahlvorschlag1.ergebnis +
          ergebnis3Wahlvorschlag1.ergebnis
      );
      expect(result.get("wahlvorschlag2")).toStrictEqual(
        ergebnis1Wahlvorschlag2.ergebnis + ergebnis2Wahlvorschlag2.ergebnis
      );
      expect(result.get("wahlvorschlag3")).toStrictEqual(
        ergebnis1Wahlvorschlag3.ergebnis
      );
    });

    it("should_ignoreErgebnisse_when_wahlvorschlagIDIsNull", () => {
      const ergebnis1Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(6)
        .build() as ErgebnisWithErgebnis;
      const ergebnis2Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(8)
        .build() as ErgebnisWithErgebnis;
      const ergebnis3Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(12)
        .build() as ErgebnisWithErgebnis;
      const ergebnis1WahlvorschlagNull = prepareErgebnis()
        .wahlvorschlagID(null)
        .ergebnis(9)
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse()
          .ergebnisse([
            ergebnis1Wahlvorschlag1,
            ergebnis2Wahlvorschlag1,
            ergebnis1WahlvorschlagNull,
            ergebnis3Wahlvorschlag1,
          ])
          .build()
      );

      const result = unitUnderTest.stapelCGueltigErgebnisseSums.value;

      expect(result.size).toStrictEqual(1);
      expect(result.get("wahlvorschlag1")).toStrictEqual(
        ergebnis1Wahlvorschlag1.ergebnis +
          ergebnis2Wahlvorschlag1.ergebnis +
          ergebnis3Wahlvorschlag1.ergebnis
      );
    });

    it("should_ignoreErgebnisse_when_ergebnisIsNull", () => {
      const ergebnis1Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(6)
        .build() as ErgebnisWithErgebnis;
      const ergebnis2Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(8)
        .build() as ErgebnisWithErgebnis;
      const ergebnis3Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(12)
        .build() as ErgebnisWithErgebnis;
      const ergebnisNullWahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(null)
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse()
          .ergebnisse([
            ergebnis1Wahlvorschlag1,
            ergebnis2Wahlvorschlag1,
            ergebnisNullWahlvorschlag1,
            ergebnis3Wahlvorschlag1,
          ])
          .build()
      );

      const result = unitUnderTest.stapelCGueltigErgebnisseSums.value;

      expect(result.size).toStrictEqual(1);
      expect(result.get("wahlvorschlag1")).toStrictEqual(
        ergebnis1Wahlvorschlag1.ergebnis +
          ergebnis2Wahlvorschlag1.ergebnis +
          ergebnis3Wahlvorschlag1.ergebnis
      );
    });
  });

  describe("wahlvorschlaege", () => {
    it("should_returnArrayWithWahlvorschlaege_when_wahlvorschlaegeForWahlIdAndWahlbezirkIdAreGiven", () => {
      const wahlvorschlag1 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag1")
        .build();
      const wahlvorschlag2 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag2")
        .build();
      const wahlvorschlag3 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag3")
        .build();
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        prepareWahlvorschlaege()
          .wahlvorschlaege(
            new Set([wahlvorschlag1, wahlvorschlag2, wahlvorschlag3])
          )
          .build()
      );

      const result = unitUnderTest.wahlvorschlaege.value;
      const expectedResult = [wahlvorschlag1, wahlvorschlag2, wahlvorschlag3];
      expect(result).toStrictEqual(expectedResult);
      expect(
        mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID]]);
    });

    it("should_returnEmptyArray_when_wahlvorschlaegeForWahlIdAndWahlbezirkIdAreNotGiven", () => {
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        undefined
      );

      const result = unitUnderTest.wahlvorschlaege.value;
      expect(result).toStrictEqual([]);
    });

    it("should_returnEmptyArray_when_wahlvorschlaegeForWahlIdAndWahlbezirkIdHasNoWahlvorschlaeage", () => {
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        prepareWahlvorschlaege()
          .wahlvorschlaege(new Set<Wahlvorschlag>([]))
          .build()
      );

      const result = unitUnderTest.wahlvorschlaege.value;
      expect(result).toStrictEqual([]);
    });
  });

  describe("wahlvorschlaegeAndSumAboveZero", () => {
    it("should_returnSubsetOfWahlvorschlaegeWithErgebnis_when_gueltigErgebnisForWahlvorschlagExists", () => {
      const wahlvorschlag1 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag1")
        .build();
      const wahlvorschlag2 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag2")
        .build();
      const wahlvorschlag3 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag3")
        .build();
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        prepareWahlvorschlaege()
          .wahlvorschlaege(
            new Set([wahlvorschlag1, wahlvorschlag2, wahlvorschlag3])
          )
          .build()
      );

      const ergebnis1Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag1.identifikator)
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag1.identifikator)
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis1Wahlvorschlag2 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag2.identifikator)
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        prepareErgebnisse()
          .ergebnisse([
            ergebnis1Wahlvorschlag1,
            ergebnis2Wahlvorschlag1,
            ergebnis1Wahlvorschlag2,
          ])
          .build()
      );

      const result = unitUnderTest.wahlvorschlaegeAndSumAboveZero.value;

      const expectedResult = [
        {
          wahlvorschlag: wahlvorschlag1,
          sum:
            ergebnis1Wahlvorschlag1.ergebnis + ergebnis2Wahlvorschlag1.ergebnis,
        },
        {
          wahlvorschlag: wahlvorschlag2,
          sum: ergebnis1Wahlvorschlag2.ergebnis,
        },
      ];
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnEmptyArray_when_noWahlvorschlaegeAreGiven", () => {
      const ergebnis1Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(generateRandomNumber(4))
        .build();
      const ergebnis2Wahlvorschlag1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(generateRandomNumber(4))
        .build();
      const ergebnis1Wahlvorschlag2 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag2")
        .ergebnis(generateRandomNumber(4))
        .build();
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          ergebnis1Wahlvorschlag1,
          ergebnis2Wahlvorschlag1,
          ergebnis1Wahlvorschlag2,
        ])
        .build();
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockImplementation(
        createMockImplementationForGetErgebnisseByWahlIdAndStapelartOrUndefinedWithErgebnisseForStapelArt(
          new Map([[StapelArtEnum.ObwCGueltig, gueltige]])
        )
      );

      const result = unitUnderTest.wahlvorschlaegeAndSumAboveZero.value;
      expect(result).toStrictEqual([]);
    });

    it("should_returnEmptyArray_when_noGueltigeErgebnisseAreGiven", () => {
      const wahlvorschlag1 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag1")
        .build();
      const wahlvorschlag2 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag2")
        .build();
      const wahlvorschlag3 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag3")
        .build();
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        prepareWahlvorschlaege()
          .wahlvorschlaege(
            new Set([wahlvorschlag1, wahlvorschlag2, wahlvorschlag3])
          )
          .build()
      );

      const result = unitUnderTest.wahlvorschlaegeAndSumAboveZero.value;
      expect(result).toStrictEqual([]);
    });
  });

  describe("switchStapelCOfErgebnis", () => {
    it("should_switchToObwCGueltig_when_setStapelUngueltigIsFalse", () => {
      const ergebnis = prepareErgebnis()
        .numIndex(generateRandomNumber(4))
        .build() as ErgebnisWithNumIndex;
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));
      unitUnderTest.switchStapelCOfErgebnis(
        { stapelArt: stapelArt, ergebnis },
        false
      );

      expect(mockDefinitions.switchStapelOfErgebnis.mock.calls).toStrictEqual([
        [
          { wahlID, wahlbezirkID, stapelArt },
          ergebnis.numIndex,
          StapelArtEnum.ObwCGueltig,
        ],
      ]);
    });

    it("should_switchToObwCUngueltig_when_setStapelUngueltigIsTrue", () => {
      const ergebnis = prepareErgebnis()
        .numIndex(generateRandomNumber(4))
        .build() as ErgebnisWithNumIndex;
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));
      unitUnderTest.switchStapelCOfErgebnis(
        { stapelArt: stapelArt, ergebnis },
        true
      );

      expect(mockDefinitions.switchStapelOfErgebnis.mock.calls).toStrictEqual([
        [
          { wahlID, wahlbezirkID, stapelArt },
          ergebnis.numIndex,
          StapelArtEnum.ObwCUngueltig,
        ],
      ]);
    });
  });

  describe("totalSum", () => {
    it("should_buildSumOfGueltigAndUngueltig_when_valuesAreGiven", () => {
      const gueltig1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(42)
        .build() as ErgebnisWithErgebnis;
      const gueltig2 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag2")
        .ergebnis(23)
        .build() as ErgebnisWithErgebnis;
      const gueltige = prepareErgebnisse()
        .ergebnisse([gueltig1, gueltig2])
        .build();

      const ungueltig1 = prepareErgebnis()
        .ergebnis(11)
        .build() as ErgebnisWithErgebnis;
      const ungueltig2 = prepareErgebnis()
        .ergebnis(12)
        .build() as ErgebnisWithErgebnis;
      const ungueltige = prepareErgebnisse()
        .ergebnisse([ungueltig1, ungueltig2])
        .build();

      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockImplementation(
        createMockImplementationForGetErgebnisseByWahlIdAndStapelartOrUndefinedWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.totalSum.value;

      expect(result).toStrictEqual(
        gueltig1.ergebnis +
          gueltig2.ergebnis +
          ungueltig1.ergebnis +
          ungueltig2.ergebnis
      );
    });

    it("should_useZero_when_stapelCGueltigIsEmptyArray", () => {
      const ungueltig1 = prepareErgebnis()
        .ergebnis(11)
        .build() as ErgebnisWithErgebnis;
      const ungueltig2 = prepareErgebnis()
        .ergebnis(12)
        .build() as ErgebnisWithErgebnis;
      const ungueltige = prepareErgebnisse()
        .ergebnisse([ungueltig1, ungueltig2])
        .build();
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockImplementation(
        createMockImplementationForGetErgebnisseByWahlIdAndStapelartOrUndefinedWithErgebnisseForStapelArt(
          new Map([[StapelArtEnum.ObwCUngueltig, ungueltige]])
        )
      );

      const result = unitUnderTest.totalSum.value;

      expect(result).toStrictEqual(ungueltig1.ergebnis + ungueltig2.ergebnis);
    });

    it("should_useZero_when_stapelCUngueltigIsEmptyArray", () => {
      const gueltig1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .ergebnis(42)
        .build() as ErgebnisWithErgebnis;
      const gueltig2 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag2")
        .ergebnis(23)
        .build() as ErgebnisWithErgebnis;
      const gueltige = prepareErgebnisse()
        .ergebnisse([gueltig1, gueltig2])
        .build();
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockImplementation(
        createMockImplementationForGetErgebnisseByWahlIdAndStapelartOrUndefinedWithErgebnisseForStapelArt(
          new Map([[StapelArtEnum.ObwCGueltig, gueltige]])
        )
      );

      const result = unitUnderTest.totalSum.value;

      expect(result).toStrictEqual(gueltig1.ergebnis + gueltig2.ergebnis);
    });
  });
});
