import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  MAX_LENGTH,
  MAX_NUMBER,
  MIN_LENGTH,
  MIN_NUMBER,
  NO_NEGATIVE_INPUT,
  REQUIRED,
  TIME_GREATER_OR_EQUAL,
  TIME_LESS_OR_EQUAL,
  TIME_NOT_IN_FUTURE,
} from "@/util/rules";

describe("Validation rules", () => {
  describe("RULE_MAX_LENGTH", () => {
    const rule = MAX_LENGTH(10);

    it("should_returnErrorMessage_when_inputTooLong", () => {
      expect(rule("tooLongString")).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputShortEnough", () => {
      expect(rule("short")).toStrictEqual(true);
    });
  });

  describe("RULE_MIN_LENGTH", () => {
    const rule = MIN_LENGTH(5);

    it("should_returnErrorMessage_when_inputTooShort", () => {
      expect(rule("t")).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputLongEnough", () => {
      expect(rule("stringLongEnough")).toStrictEqual(true);
    });
  });

  describe("RULE_MIN_NUMBER", () => {
    const rule = MIN_NUMBER(5);

    it("should_returnErrorMessage_when_inputTooSmall", () => {
      expect(rule(2)).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputBigEnough", () => {
      expect(rule(7)).toStrictEqual(true);
    });
  });

  describe("RULE_MAX_NUMBER", () => {
    const rule = MAX_NUMBER(5);

    it("should_returnErrorMessage_when_inputTooBig", () => {
      expect(rule(7)).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputSmallEnough", () => {
      expect(rule(2)).toStrictEqual(true);
    });
  });

  describe("RULE_NO_NEGATIVE_INPUT", () => {
    const rule = NO_NEGATIVE_INPUT;

    it("should_returnErrorMessage_when_inputNegative", () => {
      expect(rule(-2)).toBeTypeOf("string");
    });
    it("should_returnTrue_when_inputPositive", () => {
      expect(rule(2)).toStrictEqual(true);
    });
  });

  describe("RULE_REQUIRED", () => {
    const rule = REQUIRED;

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

  describe("RULE_TIME_NOT_IN_FUTURE", () => {
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

    const rule = TIME_NOT_IN_FUTURE;

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

  describe("RULE_TIME_GREATER_OR_EQUAL", () => {
    const rule = TIME_GREATER_OR_EQUAL("08:00");

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

  describe("RULE_TIME_LESS_OR_EQUAL", () => {
    const rule = TIME_LESS_OR_EQUAL("16:00");

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
