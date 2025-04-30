import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const mockedNow = new Date();

describe("dateTimeFormatter.ts", () => {
  const { time, toCorrectTimezone, getDateFromTimeString } =
    useDateTimeFormatter();

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
      const dateWithTwoDigitTimeParts = new Date("2025-02-18T04:00:03");

      const result = time(dateWithTwoDigitTimeParts);

      expect(result).toStrictEqual("04:00:03");
    });
  });

  describe("toCorrectTimeZone", () => {
    it.each([
      { time: "2025-04-29T12:12:07.855Z", when: "DateString" },
      { time: new Date("2025-04-29T12:12:07.855Z"), when: "Date" },
    ])("should_returnDateWithCorrectTime_when_given$when", async ({ time }) => {
      const result = toCorrectTimezone(time);

      // get local time
      const localHours =
        result.getUTCHours() - Math.trunc(result.getTimezoneOffset() / 60);
      const localMinutes = result.getUTCMinutes();

      // expected time in ISO-format
      const expectedTime = new Date(result);
      expectedTime.setHours(localHours, localMinutes);

      expect(result.toISOString()).toContain(
        expectedTime.toISOString().substring(11, 16)
      );
    });

    it("should_returnDateWithUndefinedTime_when_inputStringIsNoDateString", () => {
      const result = toCorrectTimezone("text");
      expect(result).toBeInstanceOf(Date);
      expect(isNaN(result.getTime())).toBe(true);
    });
  });

  describe("getDateFromTimeString", () => {
    it("should_returnDateWithGivenTime_when_givenTimeString", () => {
      const input = "12:12";
      const result = getDateFromTimeString(input).toString();
      expect(result).toContain("12:12");
    });

    it("should_throwError_when_inputStringIsNoTimeString", () => {
      const result = getDateFromTimeString("text");
      expect(result).toBeInstanceOf(Date);
      expect(isNaN(result.getTime())).toBe(true);
    });
  });
});
