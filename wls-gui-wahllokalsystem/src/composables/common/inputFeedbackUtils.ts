import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

export function useInputFeedbackUtils() {
  function getIconForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "$invalid";
      case InputFeedbackTypeEnum.information:
        return "$information";
    }
  }

  function getIconColorForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "error";
      case InputFeedbackTypeEnum.information:
        return "info";
    }
  }

  function getTextColorForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "text-error";
      case InputFeedbackTypeEnum.information:
        return "text-info";
    }
  }

  function getBorderColorForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "border-error";
      case InputFeedbackTypeEnum.information:
        return "border-info";
    }
  }

  return {
    getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType,
    getIconForInputFeedbackType,
    getTextColorForInputFeedbackType,
  };
}
