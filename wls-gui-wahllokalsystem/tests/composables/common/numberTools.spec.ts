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

  describe("sortAndGroupConsecutiveNumbers", () => {
    it.each([
      [[], []],
      [[4], ["4"]],
      [[3, 1, 2], ["1-3"]],
      [
        [1, 2, 5, 3, 7, 10, 9, 8, 13, 19, 15, 20],
        ["1-3", "5", "7-10", "13", "15", "19-20"],
      ],
    ])(
      "should_sortAndGroup'%s'_when_numbersAre'%s'",
      (numbers, expectedResult) => {
        expect(
          unitUnderTest.sortAndGroupConsecutiveNumbers(numbers)
        ).toStrictEqual(expectedResult);
      }
    );
  });
});
