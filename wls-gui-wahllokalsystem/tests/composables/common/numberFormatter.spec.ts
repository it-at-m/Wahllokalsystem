import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";

describe("numberFormatter.ts", () => {
  const { convertToSixDigitArray } = useNumberFormatter();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("convertToSixDigitArray", () => {
    it.each([
      { input: 0, output: ["0", "0", "0", "0", "0", "0"] },
      { input: 2, output: ["0", "0", "0", "0", "0", "2"] },
      { input: 16, output: ["0", "0", "0", "0", "1", "6"] },
      { input: 255, output: ["0", "0", "0", "2", "5", "5"] },
      { input: 8476, output: ["0", "0", "8", "4", "7", "6"] },
      { input: 38562, output: ["0", "3", "8", "5", "6", "2"] },
      { input: 498536, output: ["4", "9", "8", "5", "3", "6"] },
    ])(
      "should_returnArrayWithLeadingZeroes'$output'_when_givenInput'$input'",
      ({ input, output }) => {
        expect(convertToSixDigitArray(input)).toStrictEqual(output);
      }
    );
  });
});
