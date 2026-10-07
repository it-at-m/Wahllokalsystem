import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useMbwErgebnisseAndWahlvorschlagTools } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagTools.ts";

const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();
const { generateRandomNumber } = useCommonTestDataFactory();

describe("mbwErgebnisseAndWahlvorschlagTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useMbwErgebnisseAndWahlvorschlagTools>;

  beforeEach(() => {
    unitUnderTest = useMbwErgebnisseAndWahlvorschlagTools();
  });

  describe("createWithErgebnissen", () => {
    it("should_returnErgebnisseForBothStapel_when_givenWahlvorschlagAndErgebnisse", () => {
      const wahlvorschlag = createWahlvorschlag();

      const ergebnisStapelA = generateRandomNumber(3);
      const ergebnisStapelB = generateRandomNumber(3);
      expect(
        unitUnderTest.createWithErgebnissen(
          wahlvorschlag,
          ergebnisStapelA,
          ergebnisStapelB
        )
      ).toStrictEqual({
        wahlvorschlag: wahlvorschlag,
        ergebnisStapelA: {
          ergebnis: ergebnisStapelA,
          wahlvorschlagsOrdnungszahl: null,
          wahlvorschlagID: null,
          numIndex: null,
          kandidatID: null,
        },
        ergebnisStapelB: {
          ergebnis: ergebnisStapelB,
          wahlvorschlagsOrdnungszahl: null,
          wahlvorschlagID: null,
          numIndex: null,
          kandidatID: null,
        },
      });
    });

    it("should_retainWahlvorschlagReference_when_givenWahlvorschlagAndErgebnisse", () => {
      const wahlvorschlag = createWahlvorschlag();

      const result = unitUnderTest.createWithErgebnissen(wahlvorschlag, 1, 2);

      expect(result.wahlvorschlag).toBe(wahlvorschlag);
    });

    it("should_createSeparateErgebnisseWithZeroValues_when_givenZeroForBothStapel", () => {
      const result = unitUnderTest.createWithErgebnissen(
        createWahlvorschlag(),
        0,
        0
      );

      expect(result.ergebnisStapelA.ergebnis).toBe(0);
      expect(result.ergebnisStapelB.ergebnis).toBe(0);
      expect(result.ergebnisStapelA).not.toBe(result.ergebnisStapelB);
    });
  });
});
