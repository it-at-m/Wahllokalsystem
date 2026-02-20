import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const mockedNow = new Date();

describe("dateTimeFormatter.ts", () => {
  const {
    toHhMmSs,
    toGermanDate,
    toGermanDateWithLongMonth,
    toYyyyMmDd,
    toYyyyMmDdWithTimeWithoutTimezoneOffset,
    toTimeWithHoursAndOptionalMinutes,
  } = useDateTimeFormatter();

  beforeEach(() => {
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe("toHhMmSs", () => {
    it("should_returnEmptyString_when_parameterIsNull", () => {
      const result = toHhMmSs(null);

      expect(result).toStrictEqual("");
    });

    it("should_returnEmptyString_when_parameterIsUndefined", () => {
      const result = toHhMmSs(undefined);

      expect(result).toStrictEqual("");
    });

    it("should_returnStringWithoutPadding_when_eachTimePartHasTwoDigits", () => {
      const dateWithTwoDigitTimeParts = new Date("2025-02-18T14:10:23");

      const result = toHhMmSs(dateWithTwoDigitTimeParts);

      expect(result).toStrictEqual("14:10:23");
    });

    it("should_returnStringWithPaddingZeros_when_timePartHasNotTwoDigits", () => {
      const dateWithoutTwoDigitTimeParts = new Date("2025-02-18T04:00:03");

      const result = toHhMmSs(dateWithoutTwoDigitTimeParts);

      expect(result).toStrictEqual("04:00:03");
    });
  });

  describe("toGermanDate", () => {
    it.each([
      "2026-01-01",
      "2026/01/01",
      "01.01.2026",
      "2026-01-01T00:00:00Z",
      "2026-1-1",
    ])(
      "should_returnDateStringInLocalFormat_when_givenValidDateString'%s'",
      (datestring) => {
        expect(toGermanDate(datestring)).toBe("01.01.2026");
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
        expect(toGermanDate(datestring)).toBe(undefined);
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
        expect(toGermanDate(datestring)).toBe(undefined);
      }
    );
  });

  describe("toYyyyMmDd", () => {
    it.each([
      { dateStringToParse: "2025-07-30", expectedDateString: "2025-07-30" },
      { dateStringToParse: "2025-12-30", expectedDateString: "2025-12-30" },
      {
        dateStringToParse: "2025-07-30T00:00:00.000",
        expectedDateString: "2025-07-30",
      },
      {
        dateStringToParse: "2025-07-30T23:59:59.999",
        expectedDateString: "2025-07-30",
      },
      { dateStringToParse: "0100-01-01", expectedDateString: "0100-01-01" },
      { dateStringToParse: "0010-01-01", expectedDateString: "0010-01-01" },
      { dateStringToParse: "0001-01-01", expectedDateString: "0001-01-01" },
    ])(
      "should_returnDateOnlyAsIsoDate_when_dateIsGivenAsString'$dateStringToParse'",
      ({ dateStringToParse, expectedDateString }) => {
        const result = toYyyyMmDd(new Date(dateStringToParse));
        expect(result).toStrictEqual(expectedDateString);
      }
    );

    it("should_returnEmptyString_when_dateIsInvalid", () => {
      const result = toYyyyMmDd(new Date("2025-29-45"));
      expect(result).toStrictEqual("");
    });
  });

  describe("toYyyyMmDdWithTimeWithoutTimezoneOffset", () => {
    it.each([
      {
        dateStringToParse: "2025-07-30T01:01:01.001",
        expectedDateString: "2025-07-30T01:01:01.001",
      },
      {
        dateStringToParse: "2025-07-30T01:01:01",
        expectedDateString: "2025-07-30T01:01:01.000",
      },
      {
        dateStringToParse: "2025-07-30T01:01",
        expectedDateString: "2025-07-30T01:01:00.000",
      },
      {
        dateStringToParse: "2025-12-30T11:12:13.654",
        expectedDateString: "2025-12-30T11:12:13.654",
      },
      {
        dateStringToParse: "2025-07-30T00:00:00.000",
        expectedDateString: "2025-07-30T00:00:00.000",
      },
      {
        dateStringToParse: "2025-07-30T23:59:59.999",
        expectedDateString: "2025-07-30T23:59:59.999",
      },
      {
        dateStringToParse: "0100-01-01T00:00:00.000",
        expectedDateString: "0100-01-01T00:00:00.000",
      },
      {
        dateStringToParse: "0010-01-01T00:00:00.000",
        expectedDateString: "0010-01-01T00:00:00.000",
      },
      {
        dateStringToParse: "0001-01-01T00:00:00.000",
        expectedDateString: "0001-01-01T00:00:00.000",
      },
    ])(
      "should_returnDateOnlyAsIsoDate_when_dateIsGivenAsString'$dateStringToParse'",
      ({ dateStringToParse, expectedDateString }) => {
        const result = toYyyyMmDdWithTimeWithoutTimezoneOffset(
          new Date(dateStringToParse)
        );
        expect(result).toStrictEqual(expectedDateString);
      }
    );

    it("should_returnEmptyString_when_dateIsInvalid", () => {
      const result = toYyyyMmDdWithTimeWithoutTimezoneOffset(
        new Date("2025-29-45")
      );
      expect(result).toStrictEqual("");
    });
  });

  describe("toTimeWithHoursAndOptionalMinutes", () => {
    it.each([
      {
        dateToParse: new Date("2025-02-18T15:00:00"),
        expectedTimeString: "15",
      },
      {
        dateToParse: new Date("2025-02-18T22:22:00"),
        expectedTimeString: "22:22",
      },
      {
        dateToParse: new Date("2025-02-18T12:01"),
        expectedTimeString: "12:01",
      },
      {
        dateToParse: new Date(""),
        expectedTimeString: "",
      },
    ])(
      "should_returnTimeOnlyWithMinutes_when_minutesUnequal00'$dateToParse'",
      ({ dateToParse, expectedTimeString }) => {
        const result = toTimeWithHoursAndOptionalMinutes(dateToParse);
        expect(result).toStrictEqual(expectedTimeString);
      }
    );
  });
});
