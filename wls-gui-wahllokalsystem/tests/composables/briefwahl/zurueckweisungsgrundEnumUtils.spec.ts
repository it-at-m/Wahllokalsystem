import { beforeEach, describe, expect, it } from "vitest";

import { useZurueckweisungsgrundEnumUtils } from "@/composables/briefwahl/zurueckweisungsgrundEnumUtils.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

describe("zurueckweisungsgrundEnumUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useZurueckweisungsgrundEnumUtils>;

  beforeEach(() => {
    unitUnderTest = useZurueckweisungsgrundEnumUtils();
  });
  describe("isRejectingZurueckweisungsgrund", () => {
    it("should_returnFalse_when_zurueckweisungsGrundIsNull", () => {
      expect(unitUnderTest.isRejectingZurueckweisungsgrund(null)).toStrictEqual(
        false
      );
    });

    it("should_returnTrue_when_zurueckweisungsGrundIsNotZugelassen", () => {
      const nonZugelassenGruende = Object.values(
        ZurueckweisungsgrundEnum
      ).filter((grund) => grund !== ZurueckweisungsgrundEnum.Zugelassen);

      nonZugelassenGruende.forEach((grund) => {
        expect(
          unitUnderTest.isRejectingZurueckweisungsgrund(grund)
        ).toStrictEqual(true);
      });
    });

    it("should_returnFalse_when_zurueckweisungsGrundIsZugelassen", () => {
      expect(
        unitUnderTest.isRejectingZurueckweisungsgrund(
          ZurueckweisungsgrundEnum.Zugelassen
        )
      ).toStrictEqual(false);
    });
  });
});
