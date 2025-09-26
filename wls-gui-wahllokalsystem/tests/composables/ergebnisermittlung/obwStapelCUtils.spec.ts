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

const { generateRandomNumber, getRandomItem, generateRandomString } =
  useCommonTestDataFactory();
const { createErgebnis, prepareErgebnis, prepareErgebnisse } =
  useErgebnisseTestDataFactory();
const { prepareWahlvorschlag, prepareWahlvorschlaege } =
  useWahlvorschlaegeTestDataFactory();

type ErgebnisWithErgebnis = Ergebnis & { ergebnis: number };
type ErgebnisWithNumIndex = Ergebnis & { numIndex: number };

const mockDefinitions = vi.hoisted(() => ({
  deleteErgebnisseWithNumIndexAbove: vi.fn(),
  getErgebnisseAndCreateIfMissing: vi.fn(),
  getWahlvorschlaegeByWahlIDAndWahlbezirkID: vi.fn(),
  sendErgebnisseByStapelArt: vi.fn(),
  switchStapelOfErgebnis: vi.fn(),
}));

vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: () => ({
    deleteErgebnisseWithNumIndexAbove:
      mockDefinitions.deleteErgebnisseWithNumIndexAbove,
    getErgebnisseAndCreateIfMissing:
      mockDefinitions.getErgebnisseAndCreateIfMissing,
    sendErgebnisseByStapelArt: mockDefinitions.sendErgebnisseByStapelArt,
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

  function createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
    ergebnisse: Map<StapelArtEnum, Ergebnisse>
  ) {
    return (args: {
      wahlID: string;
      wahlbezirkID: string;
      stapelArt: StapelArtEnum;
    }) => {
      return ergebnisse.get(args.stapelArt);
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
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.useRealTimers();
  });

  describe("isSaving", () => {
    it("should_getUpdated_when_saveErgebnisseIsCalled", async () => {
      const timeout = 100;

      mockDefinitions.sendErgebnisseByStapelArt.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);

      const saveErgebnissePromise = unitUnderTest.saveErgebnisse();
      expect(unitUnderTest.isSaving.value).toStrictEqual(true);

      vi.advanceTimersByTime(2 * timeout);
      await saveErgebnissePromise;

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);
    });
    it("should_getUpdated_when_saveErgebnisseIsCalledAndRequestFail", async () => {
      const timeout = 100;

      mockDefinitions.sendErgebnisseByStapelArt.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject(new Error("mocked api request failed"));
          }, timeout);
        })
      );

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);

      const saveErgebnissePromise = unitUnderTest.saveErgebnisse();
      expect(unitUnderTest.isSaving.value).toStrictEqual(true);

      vi.advanceTimersByTime(2 * timeout);
      await saveErgebnissePromise;

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);
    });
  });

  describe("stapelCUngueltigErgebnisse", () => {
    it("should_returnArrayOfErgebnisse_when_ergebnisseForWahlIDAndStapelObwCUngueltigsAreGiven", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisse.value;

      const expectedResult: ErgebnisAndStapelArt[] = [
        { ergebnis: ergebnis1, stapelArt: StapelArtEnum.ObwCUngueltig },
        { ergebnis: ergebnis2, stapelArt: StapelArtEnum.ObwCUngueltig },
      ];
      expect(result).toStrictEqual(expectedResult);
      expect(
        mockDefinitions.getErgebnisseAndCreateIfMissing.mock.calls
      ).toStrictEqual([
        [{ wahlID, wahlbezirkID, stapelArt: StapelArtEnum.ObwCUngueltig }],
      ]);
    });

    it("should_returnEmptyArray_when_noErgebnisseForWahlIDAndStapelObwCUngueltigAreGiven", () => {
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        createErgebnisseWithNoErgebnisse()
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(
        mockDefinitions.getErgebnisseAndCreateIfMissing.mock.calls
      ).toStrictEqual([
        [{ wahlID, wahlbezirkID, stapelArt: StapelArtEnum.ObwCUngueltig }],
      ]);
      expect(result).toStrictEqual(ergebnis1.ergebnis + ergebnis2.ergebnis);
    });

    it("should_returnZero_when_ergebnisseStapelCUngueltigIsEmptyArray", () => {
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        createErgebnisseWithNoErgebnisse()
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse()
          .ergebnisse([ergebnis1, ergebnis2, ergebnis3])
          .build()
      );

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(result).toStrictEqual(ergebnis1.ergebnis + ergebnis3.ergebnis);
    });
  });

  describe("stapelCGueltigErgebnisse", () => {
    it("should_returnArrayOfErgebnisse_when_ergebnisseForWahlIDAndStapelObwCGueltigsAreGiven", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result = unitUnderTest.stapelCGueltigErgebnisse.value;

      const expectedResult: ErgebnisAndStapelArt[] = [
        { ergebnis: ergebnis1, stapelArt: StapelArtEnum.ObwCGueltig },
        { ergebnis: ergebnis2, stapelArt: StapelArtEnum.ObwCGueltig },
      ];
      expect(result).toStrictEqual(expectedResult);
      expect(
        mockDefinitions.getErgebnisseAndCreateIfMissing.mock.calls
      ).toStrictEqual([
        [{ wahlID, wahlbezirkID, stapelArt: StapelArtEnum.ObwCGueltig }],
      ]);
    });

    it("should_returnEmptyArray_when_noErgebnisseForWahlIDAndStapelObwCUngueltigAreGiven", () => {
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        createErgebnisseWithNoErgebnisse()
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
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

      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, createErgebnisseWithNoErgebnisse()],
            [StapelArtEnum.ObwCUngueltig, createErgebnisseWithNoErgebnisse()],
          ])
        )
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

  describe("getStapelCGueltigErgebnisseByWahlvorschlagIdOrZero", () => {
    it("should_returnStapelCGueltigErgebnis_when_stapelCGueltigErgebnisseHasEntryForWahlvorschlagID", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .wahlvorschlagID(generateRandomString(8))
        .build();
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .wahlvorschlagID(generateRandomString(8))
        .build();

      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result1 =
        unitUnderTest.getStapelCGueltigErgebnisseByWahlvorschlagIdOrZero(
          ergebnis1.wahlvorschlagID
        );
      const result2 =
        unitUnderTest.getStapelCGueltigErgebnisseByWahlvorschlagIdOrZero(
          ergebnis2.wahlvorschlagID
        );

      expect(result1).toStrictEqual(ergebnis1.ergebnis);
      expect(result2).toStrictEqual(ergebnis2.ergebnis);
    });

    it("should_return0_when_stapelCGueltigErgebnisseHasNoEntryForWahlvorschlagID", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .wahlvorschlagID(generateRandomString(8))
        .build();
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .wahlvorschlagID(generateRandomString(8))
        .build();

      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse().ergebnisse([ergebnis1, ergebnis2]).build()
      );

      const result =
        unitUnderTest.getStapelCGueltigErgebnisseByWahlvorschlagIdOrZero(
          generateRandomString(8)
        );

      expect(result).toStrictEqual(0);
    });

    it("should_return0_when_stapelCGueltigErgebnisseIsEmptyArray", () => {
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        prepareErgebnisse().ergebnisse([]).build()
      );

      const result =
        unitUnderTest.getStapelCGueltigErgebnisseByWahlvorschlagIdOrZero(
          generateRandomString(8)
        );

      expect(result).toStrictEqual(0);
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

      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCUngueltig, ungueltige],
            [StapelArtEnum.ObwCGueltig, createErgebnisseWithNoErgebnisse()],
          ])
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
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCUngueltig, createErgebnisseWithNoErgebnisse()],
            [StapelArtEnum.ObwCGueltig, gueltige],
          ])
        )
      );

      const result = unitUnderTest.totalSum.value;

      expect(result).toStrictEqual(gueltig1.ergebnis + gueltig2.ergebnis);
    });
  });

  describe("addGueltigErgebnisse", () => {
    it("should_addGueltigErgebnisseWithNewAmount_when_noErgebnisseExist", () => {
      const mockedErgebnisse = createErgebnisseWithNoErgebnisse();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        mockedErgebnisse
      );

      unitUnderTest.addGueltigErgebnisse(3);

      const expectedErgebnisse = [
        prepareErgebnis()
          .ergebnis(1)
          .wahlvorschlagID(null)
          .wahlvorschlagsOrdnungszahl(null)
          .numIndex(1)
          .kandidatID(null)
          .build(),
        prepareErgebnis()
          .ergebnis(1)
          .wahlvorschlagID(null)
          .wahlvorschlagsOrdnungszahl(null)
          .numIndex(2)
          .kandidatID(null)
          .build(),
        prepareErgebnis()
          .ergebnis(1)
          .wahlvorschlagID(null)
          .wahlvorschlagsOrdnungszahl(null)
          .numIndex(3)
          .kandidatID(null)
          .build(),
      ];
      expect(mockedErgebnisse.ergebnisse).toStrictEqual(expectedErgebnisse);
      expect(
        mockDefinitions.getErgebnisseAndCreateIfMissing
      ).toHaveBeenCalledWith({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.ObwCGueltig,
      });
    });
    it("should_addGueltigErgebnisseWithNewAmount_when_ergebnisseAlreadyExist", () => {
      const existingErgebnis1 = createErgebnis();
      const existingErgebnis2 = createErgebnis();
      const mockedErgebnisse = prepareErgebnisse()
        .ergebnisse([existingErgebnis1, existingErgebnis2])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockReturnValue(
        mockedErgebnisse
      );

      unitUnderTest.addGueltigErgebnisse(3);

      const expectedErgebnisse = [
        ...[existingErgebnis1, existingErgebnis2],
        prepareErgebnis()
          .ergebnis(1)
          .wahlvorschlagID(null)
          .wahlvorschlagsOrdnungszahl(null)
          .numIndex(1)
          .kandidatID(null)
          .build(),
        prepareErgebnis()
          .ergebnis(1)
          .wahlvorschlagID(null)
          .wahlvorschlagsOrdnungszahl(null)
          .numIndex(2)
          .kandidatID(null)
          .build(),
        prepareErgebnis()
          .ergebnis(1)
          .wahlvorschlagID(null)
          .wahlvorschlagsOrdnungszahl(null)
          .numIndex(3)
          .kandidatID(null)
          .build(),
      ];
      expect(mockedErgebnisse.ergebnisse).toStrictEqual(expectedErgebnisse);
    });
  });

  describe("removeErgebnisseWithNumIndexAbove", () => {
    it("should_useStoreFunctionForBothStapelC_when_called", () => {
      unitUnderTest.removeErgebnisseWithNumIndexAbove(4);

      expect(
        mockDefinitions.deleteErgebnisseWithNumIndexAbove
      ).toHaveBeenCalledWith(wahlID, StapelArtEnum.ObwCGueltig, 4);
      expect(
        mockDefinitions.deleteErgebnisseWithNumIndexAbove
      ).toHaveBeenCalledWith(wahlID, StapelArtEnum.ObwCUngueltig, 4);
      expect(
        mockDefinitions.deleteErgebnisseWithNumIndexAbove.mock.calls.length
      ).toStrictEqual(2);
    });
  });

  describe("saveErgebnisse", () => {
    it("should_callServiceForBothStapelC_when_called", () => {
      unitUnderTest.saveErgebnisse();

      mockDefinitions.sendErgebnisseByStapelArt.mockImplementation(() =>
        Promise.resolve()
      );

      expect(mockDefinitions.sendErgebnisseByStapelArt).toHaveBeenCalledWith(
        wahlID,
        StapelArtEnum.ObwCGueltig,
        true
      );
      expect(mockDefinitions.sendErgebnisseByStapelArt).toHaveBeenCalledWith(
        wahlID,
        StapelArtEnum.ObwCUngueltig,
        true
      );
    });
    it("should_callServiceForBothStapelC_when_calledEvenWhenServiceFunctionFails", () => {
      unitUnderTest.saveErgebnisse();

      mockDefinitions.sendErgebnisseByStapelArt.mockImplementation(() =>
        Promise.reject(new Error("mocked send ergebnisse failed"))
      );

      expect(mockDefinitions.sendErgebnisseByStapelArt).toHaveBeenCalledWith(
        wahlID,
        StapelArtEnum.ObwCGueltig,
        true
      );
      expect(mockDefinitions.sendErgebnisseByStapelArt).toHaveBeenCalledWith(
        wahlID,
        StapelArtEnum.ObwCUngueltig,
        true
      );
    });
  });

  describe("getMaxNumIndex", () => {
    it("should_returnMaxNumIndexOfUngueltig_when_bothStapelHaveValuesButUngueltigHasHighestNumIndex", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(1).build(),
          prepareErgebnis().numIndex(11).build(),
        ])
        .build();
      const ungueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(3).build(),
          prepareErgebnis().numIndex(12).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndex();
      expect(result).toStrictEqual(12);
    });
    it("should_returnMaxNumIndexOfGueltig_when_bothStapelHaveValuesButGueltigHasHighestNumIndex", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(1).build(),
          prepareErgebnis().numIndex(11).build(),
        ])
        .build();
      const ungueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(3).build(),
          prepareErgebnis().numIndex(10).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndex();
      expect(result).toStrictEqual(11);
    });
    it("should_returnMaxNumIndexOfUngueltig_when_onlyUngueltigHaveValues", () => {
      const ungueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(3).build(),
          prepareErgebnis().numIndex(10).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCUngueltig, ungueltige],
            [StapelArtEnum.ObwCGueltig, createErgebnisseWithNoErgebnisse()],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndex();
      expect(result).toStrictEqual(10);
    });
    it("should_returnMaxNumIndexOfGueltig_when_onlyGueltigHaveValues", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(1).build(),
          prepareErgebnis().numIndex(11).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, createErgebnisseWithNoErgebnisse()],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndex();
      expect(result).toStrictEqual(11);
    });
    it("should_returnNull_when_bothStapelHaveNoValues", () => {
      const gueltige = createErgebnisseWithNoErgebnisse();
      const ungueltige = createErgebnisseWithNoErgebnisse();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndex();
      expect(result).toStrictEqual(null);
    });
  });

  describe("getMaxNumIndexWithValueSet", () => {
    it("should_returnMaxNumIndexOfUngueltig_when_bothStapelHaveValuesButUngueltigHasHighestNumIndex", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis()
            .numIndex(1)
            .wahlvorschlagID(generateRandomString(10))
            .build(),
          prepareErgebnis()
            .numIndex(11)
            .wahlvorschlagID(generateRandomString(10))
            .build(),
          prepareErgebnis().numIndex(13).wahlvorschlagID(null).build(),
        ])
        .build();
      const ungueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(3).build(),
          prepareErgebnis().numIndex(12).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndexWithValueSet();
      expect(result).toStrictEqual(12);
    });
    it("should_returnMaxNumIndexOfGueltig_when_bothStapelHaveValuesButGueltigHasHighestNumIndexWithWahlvorschlagID", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis()
            .numIndex(1)
            .wahlvorschlagID(generateRandomString(10))
            .build(),
          prepareErgebnis()
            .numIndex(11)
            .wahlvorschlagID(generateRandomString(10))
            .build(),
        ])
        .build();
      const ungueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(3).build(),
          prepareErgebnis().numIndex(10).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndexWithValueSet();
      expect(result).toStrictEqual(11);
    });
    it("should_returnMaxNumIndexOfUngueltig_when_onlyUngueltigHaveValues", () => {
      const gueltige = createErgebnisseWithNoErgebnisse();
      const ungueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(3).build(),
          prepareErgebnis().numIndex(10).build(),
        ])
        .build();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndexWithValueSet();
      expect(result).toStrictEqual(10);
    });
    it("should_returnMaxNumIndexOfGueltig_when_onlyGueltigHaveValuesWithWahlvorschlagID", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis()
            .numIndex(1)
            .wahlvorschlagID(generateRandomString(10))
            .build(),
          prepareErgebnis()
            .numIndex(11)
            .wahlvorschlagID(generateRandomString(10))
            .build(),
        ])
        .build();
      const ungueltige = createErgebnisseWithNoErgebnisse();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndexWithValueSet();
      expect(result).toStrictEqual(11);
    });
    it("should_returnNull_when_bothStapelHaveNoValues", () => {
      const gueltige = createErgebnisseWithNoErgebnisse();
      const ungueltige = createErgebnisseWithNoErgebnisse();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndexWithValueSet();
      expect(result).toStrictEqual(null);
    });
    it("should_returnNull_when_onlyGueltigHaveValuesButNonWithWahlvorschlagID", () => {
      const gueltige = prepareErgebnisse()
        .ergebnisse([
          prepareErgebnis().numIndex(1).wahlvorschlagID(null).build(),
          prepareErgebnis().numIndex(11).wahlvorschlagID(null).build(),
        ])
        .build();
      const ungueltige = createErgebnisseWithNoErgebnisse();
      mockDefinitions.getErgebnisseAndCreateIfMissing.mockImplementation(
        createMockImplementationForGetErgebnisseAndCreateIfMissingWithErgebnisseForStapelArt(
          new Map([
            [StapelArtEnum.ObwCGueltig, gueltige],
            [StapelArtEnum.ObwCUngueltig, ungueltige],
          ])
        )
      );

      const result = unitUnderTest.getMaxNumIndexWithValueSet();
      expect(result).toStrictEqual(null);
    });
  });

  function createErgebnisseWithNoErgebnisse() {
    return prepareErgebnisse().ergebnisse([]).build();
  }
});
