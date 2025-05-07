import { describe, expect, it } from "vitest";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { isValidDate } = useDateTimeUtils();

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
});
