import { beforeEach, describe, expect, it } from "vitest";

import { useStringUtils } from "@/composables/common/stringUtils.ts";

describe("stringUtils", () => {
  let unitUnderTest: ReturnType<typeof useStringUtils>;

  beforeEach(() => {
    unitUnderTest = useStringUtils();
  });

  describe("toLowerCaseFirstLetter", () => {
    it.each([
      {
        text: "Beispieltext",
        expected: "beispieltext",
      },
      {
        text: "",
        expected: "",
      },
      {
        text: "beispieltext",
        expected: "beispieltext",
      },
      {
        text: "Ein ganzer Beispielsatz.",
        expected: "ein ganzer Beispielsatz.",
      },
    ])(
      "should_changeFirstLetterToLowerCase_when_isGiven$text",
      (testArguments) => {
        const result = unitUnderTest.toLowerCaseFirstLetter(testArguments.text);
        expect(result).toStrictEqual(testArguments.expected);
      }
    );
  });
});
