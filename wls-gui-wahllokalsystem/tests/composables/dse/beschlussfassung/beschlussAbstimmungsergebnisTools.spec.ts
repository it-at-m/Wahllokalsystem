import { beforeEach, describe, expect, it } from "vitest";

import { useBeschlussAbstimmungsergebnisTools } from "@/composables/dse/beschlussfassung/beschlussAbstimmungsergebnisTools.ts";

describe("useBeschlussAbstimmungsergebnisTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useBeschlussAbstimmungsergebnisTools>;

  beforeEach(() => {
    unitUnderTest = useBeschlussAbstimmungsergebnisTools();
  });

  describe("createEmptyAbstimmungsergebnis", () => {
    it("should_returnEmptyDetails_when_called", () => {
      const result = unitUnderTest.createEmptyAbstimmungsergebnis();

      expect(result).toStrictEqual({
        stimmenDafuer: null,
        stimmenDagegen: null,
        hasWahlvorsteherVotedDafuer: false,
        abstimmungIsUnentschieden: false,
        abstimmungIsUngueltig: false,
      });
    });
  });
});
