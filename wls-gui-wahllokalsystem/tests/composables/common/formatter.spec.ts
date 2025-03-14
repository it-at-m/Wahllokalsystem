import { describe, expect, it } from "vitest";

import useFormatter from "@/composables/common/formatter";

describe("formatter.ts", () => {
  const { time } = useFormatter();

  describe("time", () => {
    it("should_returnEmptyString_when_parameterIsNull", () => {
      const result = time(null);

      expect(result).toStrictEqual("");
    });

    it("should_returnEmptyString_when_parameterIsUndefined", () => {
      const result = time(undefined);

      expect(result).toStrictEqual("");
    });

    it("should_returnStringWithoutPadding_when_eachTimePartHasTwoDigits", () => {
      const dateWithTwoDigitTimeParts = new Date("2025-02-18T14:10:23");

      const result = time(dateWithTwoDigitTimeParts);

      expect(result).toStrictEqual("14:10:23");
    });

    it("should_returnStringWithPaddingZeros_when_timePartHasNotTwoDigits", () => {
      const dateWithTwoDigitTimeParts = new Date("2025-02-18T04:00:03");

      const result = time(dateWithTwoDigitTimeParts);

      expect(result).toStrictEqual("04:00:03");
    });
  });
});
