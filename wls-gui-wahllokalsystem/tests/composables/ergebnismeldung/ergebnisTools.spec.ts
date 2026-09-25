import { describe, expect, it } from "vitest";

import { useErgebnisTools } from "@/composables/ergebnismeldung/ergebnisTools.ts";

describe("ergebnisTools.ts", () => {
  describe("createWithErgebnisOnly", () => {
    it("should_createErgebnisWithOnlyErgebnisSet_when_givenErgebnis", () => {
      const ergebnis = 42;

      const result = useErgebnisTools().createWithErgebnisOnly(ergebnis);

      expect(result).toStrictEqual({
        ergebnis,
        wahlvorschlagsOrdnungszahl: null,
        wahlvorschlagID: null,
        numIndex: null,
        kandidatID: null,
      });
    });
  });
});
