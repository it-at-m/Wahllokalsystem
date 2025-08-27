import { describe, expect, it } from "vitest";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { isValidDate, applyLocalTimezoneOffset } = useDateTimeUtils();

describe("dateTimeUtils", () => {
  describe("isValidDate", () => {
    it("should_returnTrue_when_dateIsValid", () => {
      expect(isValidDate(new Date())).toStrictEqual(true);
    });

    it("should_returnFalse_when_dateWasInitWithInvalidString", () => {
      expect(isValidDate(new Date("some random illegal string"))).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_dateWasInitWithDateStringThatRepresentsAnIllegalDate", () => {
      expect(isValidDate(new Date("2025-02-32"))).toStrictEqual(false);
    });
  });

  describe("applyLocalTimezoneOffset", () => {
    it.each([
      { time: "2025-04-29T12:12:07.855Z", when: "DateString" },
      { time: new Date("2025-04-29T12:12:07.855Z"), when: "Date" },
    ])("should_returnDateWithCorrectTime_when_given$when", async ({ time }) => {
      const result = applyLocalTimezoneOffset(time);

      const utcDate = new Date(time);
      const localOffset = utcDate.getTimezoneOffset() * 60000;
      const expectedDate = new Date(utcDate.getTime() - localOffset);

      expect(result.toISOString()).toEqual(expectedDate.toISOString());
    });

    it("should_returnDateWithUndefinedTime_when_inputStringIsNoDateString", () => {
      const result = applyLocalTimezoneOffset("text");

      expect(result).toBeInstanceOf(Date);
      expect(isNaN(result.getTime())).toBe(true);
    });
  });
});
