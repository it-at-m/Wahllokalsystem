import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const mockedNow = new Date();

describe("dateTimeFormatter.ts", () => {
  const {
    time,
    applyLocalTimezoneOffset,
    toGermanDateFormat,
      getDateFromTimeString,
    toGermanDateWithLongMonth,
  } = useDateTimeFormatter();

  beforeEach(() => {
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

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
      const dateWithoutTwoDigitTimeParts = new Date("2025-02-18T04:00:03");

      const result = time(dateWithoutTwoDigitTimeParts);

      expect(result).toStrictEqual("04:00:03");
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

  describe("getDateFromTimeString", () => {
    it.each(["12:12", "12:12:30"])(
      "should_returnDateWithGivenTime_when_givenValidTimeString'%s'",
      (input) => {
        const result = getDateFromTimeString(input).toString();

        expect(result).toContain(input);
      }
    );

    it.each(["text", "26:12", "11:78", "23:45:67", "", " "])(
      "should_returnInvalidDate_when_inputStringIsInvalidValue'%s'",
      (input) => {
        const result = getDateFromTimeString(input);

        expect(result).toBeInstanceOf(Date);
        expect(isNaN(result.getTime())).toBe(true);
      }
    );
  });

  describe("toGermanDateFormat", () => {
    it.each([
      "2026-01-01",
      "2026/01/01",
      "01.01.2026",
      "2026-01-01T00:00:00Z",
      "2026-1-1",
    ])(
      "should_returnDateStringInLocalFormat_when_givenValidDateString'%s'",
      (datestring) => {
        expect(toGermanDateFormat(datestring)).toBe("01.01.2026");
      }
    );

    it.each([
      "2026-01-32",
      "2026-13-01",
      "2026-01-AB",
      "random string",
      "",
      " ",
    ])(
      "should_returnUndefined_when_givenInvalidDateString'%s'",
      (datestring) => {
        expect(toGermanDateFormat(datestring)).toBe(undefined);
      }
    );
  });

  describe("toGermanDateWithLongMonth", () => {
    it.each([
      "2026-01-01",
      "2026/01/01",
      "01.01.2026",
      "2026-01-01T00:00:00Z",
      "2026-1-1",
    ])(
      "should_returnDateStringInLocalFormatWithLongMonth_when_givenValidDateString'%s'",
      (datestring) => {
        expect(toGermanDateWithLongMonth(datestring)).toBe("1. Januar 2026");
      }
    );

    it.each([
      "2026-01-32",
      "2026-13-01",
      "2026-01-AB",
      "random string",
      "",
      " ",
    ])(
      "should_returnUndefined_when_givenInvalidDateString'%s'",
      (datestring) => {
        expect(toGermanDateFormat(datestring)).toBe(undefined);
      }
    );
  });
});
