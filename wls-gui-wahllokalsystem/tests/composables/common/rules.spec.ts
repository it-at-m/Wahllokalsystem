import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useRules } from "@/composables/common/rules.ts";

const {
  maxLength,
  maxNumber,
  minLength,
  minNumber,
  required,
  timeGreaterOrEqual,
  timeLessOrEqual,
  timeNotInFuture,
} = useRules();

describe("Validation rules", () => {
  describe("maxLength", () => {
    const rule = maxLength(10);

    it("should_returnErrorMessage_when_inputTooLong", () => {
      expect(rule("tooLongString")).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputShortEnough", () => {
      expect(rule("short")).toStrictEqual(true);
    });
  });

  describe("minLength", () => {
    const rule = minLength(5);

    it("should_returnErrorMessage_when_inputTooShort", () => {
      expect(rule("t")).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputLongEnough", () => {
      expect(rule("stringLongEnough")).toStrictEqual(true);
    });
  });

  describe("minNumber", () => {
    const rule = minNumber(5);

    it("should_returnErrorMessage_when_inputTooSmall", () => {
      expect(rule(2)).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputBigEnough", () => {
      expect(rule(7)).toStrictEqual(true);
    });
  });

  describe("maxNumber", () => {
    const rule = maxNumber(5);

    it("should_returnErrorMessage_when_inputTooBig", () => {
      expect(rule(7)).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputSmallEnough", () => {
      expect(rule(2)).toStrictEqual(true);
    });
  });

  describe("required", () => {
    const rule = required;

    it("should_returnTrue_when_inputStringExists", () => {
      expect(rule("input")).toStrictEqual(true);
    });

    it("should_returnTrue_when_inputNumberExists", () => {
      expect(rule(3)).toStrictEqual(true);
    });

    it.each([
      ["Blank", " "],
      ["Empty", ""],
      ["Null", null],
    ])("should_returnErrorMessage_when_input%s", (text, input) => {
      expect(rule(input)).toBeTypeOf("string");
    });
  });

  describe("timeNotInFuture", () => {
    beforeEach(() => {
      const mockedNow = new Date();
      mockedNow.setHours(15, 0);
      vi.useFakeTimers({
        now: mockedNow,
      });
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    const rule = timeNotInFuture;

    it.each(["12:23", "14:58", "15:00:00"])(
      "should_returnTrue_when_inputTime'%s'IsLessOrEqualToNow",
      (input) => {
        expect(rule(input)).toStrictEqual(true);
      }
    );

    it.each(["15:01", "19:36"])(
      "should_returnErrorMessage_when_inputTimeIsInFuture",
      (input) => {
        expect(rule(input)).toBeTypeOf("string");
      }
    );

    it.each(["30:25", "14:75", "10:20:98", "", " ", "text", "ab:cd"])(
      "should_returnErrorMessage_when_inputTime'%s'IsInvalid",
      (input) => {
        expect(rule(input)).toBeTypeOf("string");
      }
    );
  });

  describe("timeGreaterOrEqual", () => {
    const rule = timeGreaterOrEqual("08:00");

    it.each(["08:00", "23:59:59", "15:00:00"])(
      "should_returnTrue_when_inputTime'%s'IsGreaterOrEqualToComparedValue",
      (input) => {
        expect(rule(input)).toStrictEqual(true);
      }
    );

    it.each(["00:00", "07:59"])(
      "should_returnErrorMessage_when_inputTime'%s'IsLessThanComparedValue",
      (input) => {
        expect(rule(input)).toBeTypeOf("string");
      }
    );

    it.each(["30:25", "14:75", "10:20:98", "", " ", "text", "ab:cd"])(
      "should_returnErrorMessage_when_inputTime'%s'IsInvalid",
      (input) => {
        expect(rule(input)).toBeTypeOf("string");
      }
    );
  });

  describe("timeLessOrEqual", () => {
    const rule = timeLessOrEqual("16:00");

    it.each(["08:00", "12:36", "16:00:00"])(
      "should_returnTrue_when_inputTime'%s'IsLessOrEqualToComparedValue",
      (input) => {
        expect(rule(input)).toStrictEqual(true);
      }
    );

    it.each(["16:01", "19:36"])(
      "should_returnErrorMessage_when_inputTime'%s'IsGreaterThanComparedValue",
      (input) => {
        expect(rule(input)).toBeTypeOf("string");
      }
    );

    it.each(["30:25", "14:75", "10:20:98", "", " ", "text", "ab:cd"])(
      "should_returnErrorMessage_when_inputTime'%s'IsInvalid",
      (input) => {
        expect(rule(input)).toBeTypeOf("string");
      }
    );
  });
});
