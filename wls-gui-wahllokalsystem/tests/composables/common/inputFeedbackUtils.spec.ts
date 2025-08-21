import { describe, expect, it } from "vitest";

import { useInputFeedbackUtils } from "@/composables/common/inputFeedbackUtils.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

describe("inputFeedbackUtils.ts", () => {
  const {
    getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType,
    getIconForInputFeedbackType,
    getBackgroundColorAndBoldTextForInputFeedbackType,
  } = useInputFeedbackUtils();

  describe("getBorderColorForInputFeedbackType", () => {
    it.each([
      {
        inputFeedbackType: InputFeedbackTypeEnum.error,
        expectedResult: "border-error",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.information,
        expectedResult: "border-info",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.success,
        expectedResult: "border-success",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.warning,
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
        expectedResult: "info",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.success,
        expectedResult: "success",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.warning,
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
      {
        inputFeedbackType: InputFeedbackTypeEnum.success,
        expectedResult: "$valid",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.warning,
        expectedResult: "$alert",
      },
    ])(
      "should_returnCorrectValue_when_inputFeedbackType'$inputFeedbackType'IsGiven",
      (params) => {
        const result = getIconForInputFeedbackType(params.inputFeedbackType);
        expect(result).toStrictEqual(params.expectedResult);
      }
    );
  });

  describe("getBackgroundColorAndBoldTextForInputFeedbackType", () => {
    it.each([
      {
        inputFeedbackType: InputFeedbackTypeEnum.error,
        expectedResult: "bg-error font-weight-bold",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.information,
        expectedResult: "bg-info font-weight-bold",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.success,
        expectedResult: "bg-success font-weight-bold",
      },
      {
        inputFeedbackType: InputFeedbackTypeEnum.warning,
        expectedResult: "bg-warning font-weight-bold",
      },
    ])(
      "should_returnCorrectValue_when_inputFeedbackType'$inputFeedbackType'IsGiven",
      (params) => {
        const result = getBackgroundColorAndBoldTextForInputFeedbackType(
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
    { functionUnderTest: getBackgroundColorAndBoldTextForInputFeedbackType },
  ])(
    "function $functionUnderTest.name supports any enum value",
    (testSuitParams) => {
      it("should_returnNonUndefinedValue_when_anyInputFeedbackTypeIsGiven", () => {
        for (const inputFeedbackType in InputFeedbackTypeEnum) {
          const result = testSuitParams.functionUnderTest(inputFeedbackType);
          expect(
            result,
            `result for ${inputFeedbackType} was ${result}`
          ).not.toBeUndefined();
        }
      });
    }
  );
});
