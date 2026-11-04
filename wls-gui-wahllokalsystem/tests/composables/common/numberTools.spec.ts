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

  describe("groupConsecutiveNumbers", () => {
    it.each([
      [[], []],
      [[4], ["4"]],
      [[1, 2, 3], ["1-3"]],
      [
        [1, 2, 3, 5, 7, 8, 9, 10, 13, 15, 19, 20],
        ["1-3", "5", "7-10", "13", "15", "19-20"],
      ],
    ])("should_return'%s'_when_numbersAre'%s'", (numbers, expectedResult) => {
      expect(unitUnderTest.groupConsecutiveNumbers(numbers)).toStrictEqual(
        expectedResult
      );
    });
  });
});
