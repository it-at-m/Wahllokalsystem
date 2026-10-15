import { describe, expect, it } from "vitest";

import { useNumberTools } from "@/composables/common/numberTools.ts";

describe("numberTools.ts", () => {
  const unitUnderTest = useNumberTools();

  describe("zeroAsNull", () => {
    it.each([
      [0, null],
      [1, 1],
      [-1, -1],
    ])("should_return'%s'_when_valueIs'%s'", (value, expectedResult) => {
      expect(unitUnderTest.zeroAsNull(value)).toStrictEqual(expectedResult);
    });
  });
});
