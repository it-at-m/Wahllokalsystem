import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { computed } from "vue";

import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";

const { generateRandomNumber } = useCommonTestDataFactory();
const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

type ErgebnisWithErgebnis = Ergebnis & { ergebnis: number };

describe("obwStapelCUtils", () => {
  let unitUnderTest: ReturnType<typeof useOBWStapelCUtils>;

  const wahlvorschlaege: Wahlvorschlag[] = [];
  const ungueltig: Ergebnis[] = [];
  const gueltig: Ergebnis[] = [];

  beforeEach(() => {
    unitUnderTest = useOBWStapelCUtils(
      computed(() => wahlvorschlaege),
      computed(() => ungueltig),
      computed(() => gueltig)
    );
  });

  afterEach(() => {
    wahlvorschlaege.length = 0;
    ungueltig.length = 0;
    gueltig.length = 0;
  });

  describe("stapelCUngueltigErgebnisseSum", () => {
    it("should_returnSumOfUngueltigValues_when_valuesAreGiven", () => {
      const ergebnis1 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      const ergebnis2 = prepareErgebnis()
        .ergebnis(generateRandomNumber(4))
        .build() as ErgebnisWithErgebnis;
      ungueltig.push(ergebnis1, ergebnis2);

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(result).toStrictEqual(ergebnis1.ergebnis + ergebnis2.ergebnis);
    });

    it("should_returnZero_when_ergebnisseStapelCUngueltigIsEmptyArray", () => {
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
      ungueltig.push(ergebnis1, ergebnis2, ergebnis3);

      const result = unitUnderTest.stapelCUngueltigErgebnisseSum.value;

      expect(result).toStrictEqual(ergebnis1.ergebnis + ergebnis3.ergebnis);
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

      gueltig.push(
        ergebnis1Wahlvorschlag1,
        ergebnis2Wahlvorschlag1,
        ergebnis1Wahlvorschlag2,
        ergebnis1Wahlvorschlag3,
        ergebnis2Wahlvorschlag2,
        ergebnis3Wahlvorschlag1
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

      gueltig.push(
        ergebnis1Wahlvorschlag1,
        ergebnis2Wahlvorschlag1,
        ergebnis1WahlvorschlagNull,
        ergebnis3Wahlvorschlag1
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

      gueltig.push(
        ergebnis1Wahlvorschlag1,
        ergebnis2Wahlvorschlag1,
        ergebnisNullWahlvorschlag1,
        ergebnis3Wahlvorschlag1
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
      wahlvorschlaege.push(wahlvorschlag1, wahlvorschlag2, wahlvorschlag3);

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
      gueltig.push(
        ergebnis1Wahlvorschlag1,
        ergebnis2Wahlvorschlag1,
        ergebnis1Wahlvorschlag2
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
      gueltig.push(
        ergebnis1Wahlvorschlag1,
        ergebnis2Wahlvorschlag1,
        ergebnis1Wahlvorschlag2
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
      wahlvorschlaege.push(wahlvorschlag1, wahlvorschlag2, wahlvorschlag3);

      const result = unitUnderTest.wahlvorschlaegeAndSumAboveZero.value;
      expect(result).toStrictEqual([]);
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
      gueltig.push(gueltig1, gueltig2);

      const ungueltig1 = prepareErgebnis()
        .ergebnis(11)
        .build() as ErgebnisWithErgebnis;
      const ungueltig2 = prepareErgebnis()
        .ergebnis(12)
        .build() as ErgebnisWithErgebnis;
      ungueltig.push(ungueltig1, ungueltig2);

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
      ungueltig.push(ungueltig1, ungueltig2);

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
      gueltig.push(gueltig1, gueltig2);

      const result = unitUnderTest.totalSum.value;

      expect(result).toStrictEqual(gueltig1.ergebnis + gueltig2.ergebnis);
    });
  });
});
