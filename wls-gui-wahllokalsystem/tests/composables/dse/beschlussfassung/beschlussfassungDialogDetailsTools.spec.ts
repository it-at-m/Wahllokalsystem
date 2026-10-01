import { beforeEach, describe, expect, it } from "vitest";

import { useBeschlussfassungDialogDeailsTools } from "@/composables/dse/beschlussfassung/beschlussfassungDialogDetailsTools.ts";

describe("useBeschlussfassungDialogDeailsTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useBeschlussfassungDialogDeailsTools>;

  beforeEach(() => {
    unitUnderTest = useBeschlussfassungDialogDeailsTools();
  });

  describe("createEmptyBeschlussfassungDialogDetails", () => {
    it("should_returnEmptyDetails_when_called", () => {
      const result = unitUnderTest.createEmptyBeschlussfassungDialogDetails();

      expect(result).toStrictEqual({
        isGueltig: null,
        beschlussgruende: [],
        andererGrund: "",
        andererGrundChecked: false,
        beschlussText: "",
      });
    });
  });
});
