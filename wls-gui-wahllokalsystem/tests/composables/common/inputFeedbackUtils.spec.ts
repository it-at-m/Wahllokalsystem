import { describe, expect, it } from "vitest";

import { useInputFeedbackUtils } from "@/composables/common/inputFeedbackUtils.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

describe("inputFeedbackUtils.ts", () => {
  const {
    getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType,
    getIconForInputFeedbackType,
    getTextColorForInputFeedbackType,
  } = useInputFeedbackUtils();

  describe("getBorderColorForInputFeedbackType", () => {
    it.each([
      {
        inputFeedbackType: InputFeedbackTypeEnum.error,
        expectedResult: "border-error",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.information,
        expectedResult: "border-warning",
      },
    ])(
      "should_returnCorrectValue_when_inputFeedbackType'$inputFeedbackType'IsGiven",
      (params) => {
        const result = getBorderColorForInputFeedbackType(
          params.inputFeedbackType
        );
        expect(result).toStrictEqual(params.expectedResult);
      }
    );
  });

  describe("getIconColorForInputFeedbackType", () => {
    it.each([
      {
        inputFeedbackType: InputFeedbackTypeEnum.error,
        expectedResult: "error",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.information,
        expectedResult: "warning",
      },
    ])(
      "should_returnCorrectValue_when_inputFeedbackType'$inputFeedbackType'IsGiven",
      (params) => {
        const result = getIconColorForInputFeedbackType(
          params.inputFeedbackType
        );
        expect(result).toStrictEqual(params.expectedResult);
      }
    );
  });

  describe("getIconForInputFeedbackType", () => {
    it.each([
      {
        inputFeedbackType: InputFeedbackTypeEnum.error,
        expectedResult: "$invalid",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.information,
        expectedResult: "$information",
      },
    ])(
      "should_returnCorrectValue_when_inputFeedbackType'$inputFeedbackType'IsGiven",
      (params) => {
        const result = getIconForInputFeedbackType(params.inputFeedbackType);
        expect(result).toStrictEqual(params.expectedResult);
      }
    );
  });

  describe("getTextColorForInputFeedbackType", () => {
    it.each([
      {
        inputFeedbackType: InputFeedbackTypeEnum.error,
        expectedResult: "text-error",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.information,
        expectedResult: "text-warning",
      },
    ])(
      "should_returnCorrectValue_when_inputFeedbackType'$inputFeedbackType'IsGiven",
      (params) => {
        const result = getTextColorForInputFeedbackType(
          params.inputFeedbackType
        );
        expect(result).toStrictEqual(params.expectedResult);
      }
    );
  });

  describe.each([
    { functionUnderTest: getBorderColorForInputFeedbackType },
    { functionUnderTest: getIconColorForInputFeedbackType },
    { functionUnderTest: getIconForInputFeedbackType },
    { functionUnderTest: getTextColorForInputFeedbackType },
  ])(
    "function $functionUnderTest.name supports any enum value",
    (testSuitParams) => {
      it("should_returnNonUndefinedValue_when_anyInputFeedbackTypeIsGiven", () => {
        for (const inputFeedbackType in InputFeedbackTypeEnum) {
          const result = testSuitParams.functionUnderTest(inputFeedbackType);
          expect(
            getBorderColorForInputFeedbackType(inputFeedbackType),
            `result for ${inputFeedbackType} was ${result}`
          ).not.toBeUndefined();
        }
      });
    }
  );
});
