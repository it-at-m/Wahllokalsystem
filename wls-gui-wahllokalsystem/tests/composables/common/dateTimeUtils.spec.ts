import { describe, expect, it } from "vitest";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { isValidDate, createTodayWithTime } = useDateTimeUtils();

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

  describe("createTodayWithTime", () => {
    it.each(["12:12", "12:12:30"])(
      "should_returnDateWithGivenTime_when_givenValidTimeString'%s'",
      (input) => {
        const result = createTodayWithTime(input).toString();

        expect(result).toContain(input);
      }
    );

    it.each(["text", "26:12", "11:78", "23:45:67", "", " "])(
      "should_returnInvalidDate_when_inputStringIsInvalidValue'%s'",
      (input) => {
        const result = createTodayWithTime(input);

        expect(result).toBeInstanceOf(Date);
        expect(isNaN(result.getTime())).toBe(true);
      }
    );

    it.each(["12:12", "12:12:30"])(
      "should_returnDateWithZeroMilliseconds_when_givenValidTimeString'%s'",
      (input) => {
        const result = createTodayWithTime(input);

        expect(result.getMilliseconds()).toBe(0);
      }
    );
  });
});
