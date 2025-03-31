import { describe, expect, test } from "vitest";

import {
  MAX_LENGTH,
  MAX_NUMBER,
  MIN_LENGTH,
  MIN_NUMBER,
  REQUIRED,
} from "@/util/rules";

describe("Validation rules", () => {
  describe("RULE_MAX_LENGTH", () => {
    const rule = MAX_LENGTH(10);

    test("should_returnErrorMessage_when_inputTooLong", () => {
      expect(rule("tooLongString")).toBeTypeOf("string");
    });
    test("should_returnTrue_when_inputShortEnough", () => {
      expect(rule("short")).toStrictEqual(true);
    });
  });

  describe("RULE_MIN_LENGTH", () => {
    const rule = MIN_LENGTH(5);

    test("should_returnErrorMessage_when_inputTooShort", () => {
      expect(rule("t")).toBeTypeOf("string");
    });
    test("should_returnTrue_when_inputLongEnough", () => {
      expect(rule("stringLongEnough")).toStrictEqual(true);
    });
  });

  describe("RULE_MIN_NUMBER", () => {
    const rule = MIN_NUMBER(5);

    test("should_returnErrorMessage_when_inputTooSmall", () => {
      expect(rule(2)).toBeTypeOf("string");
    });
    test("should_returnTrue_when_inputBigEnough", () => {
      expect(rule(7)).toStrictEqual(true);
    });
  });

  describe("RULE_MAX_NUMBER", () => {
    const rule = MAX_NUMBER(5);

    test("should_returnErrorMessage_when_inputTooBig", () => {
      expect(rule(7)).toBeTypeOf("string");
    });
    test("should_returnTrue_when_inputSmallEnough", () => {
      expect(rule(2)).toStrictEqual(true);
    });
  });

  describe("RULE_REQUIRED", () => {
    const rule = REQUIRED;

    test("should_returnTrue_when_inputExists", () => {
      expect(rule("input")).toStrictEqual(true);
    });

    test.each([
      ["Blank", " "],
      ["Empty", ""],
      ["Null", null],
    ])("should_returnErrorMessage_when_input%s", (text, input) => {
      expect(rule(input)).toBeTypeOf("string");
    });
  });
});
