import { describe, expect, test } from "vitest";

import { MAX_LENGTH, MIN_LENGTH, REQUIRED } from "@/util/rules";

describe("Validation rules", () => {
  describe("RULE_MAX_LENGTH", () => {
    const rule = MAX_LENGTH(10);
    const ruleErrorMessage = "Maximale länge ist 10 Zeichen.";

    test("should_returnErrorMessage_when_inputTooLong", () => {
      expect(rule("tooLongString")).toStrictEqual(ruleErrorMessage);
    });
    test("should_returnTrue_when_inputShortEnough", () => {
      expect(rule("short")).toStrictEqual(true);
    });
  });

  describe("RULE_MIN_LENGTH", () => {
    const rule = MIN_LENGTH(5);
    const ruleErrorMessage = "Minimale länge ist 5 Zeichen.";

    test("should_returnErrorMessage_when_inputTooShort", () => {
      expect(rule("t")).toStrictEqual(ruleErrorMessage);
    });
    test("should_returnTrue_when_inputLongEnough", () => {
      expect(rule("stringLongEnough")).toStrictEqual(true);
    });
  });

  describe("RULE_REQUIRED", () => {
    const rule = REQUIRED;
    const ruleErrorMessage = "Feld darf nicht leer sein.";

    test("should_returnErrorMessage_when_inputEmpty", () => {
      expect(rule("")).toStrictEqual(ruleErrorMessage);
    });
    test("should_returnErrorMessage_when_inputNull", () => {
      expect(rule(null)).toStrictEqual(ruleErrorMessage);
    });
    test("should_returnTrue_when_inputExists", () => {
      expect(rule("input")).toStrictEqual(true);
    });
  });
});
