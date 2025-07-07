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
        return "warning";
    }
  }

  function getTextColorForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "text-error";
      case InputFeedbackTypeEnum.information:
        return "text-warning";
    }
  }

  function getBorderColorForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "border-error";
      case InputFeedbackTypeEnum.information:
        return "border-warning";
    }
  }

  return {
    getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType,
    getIconForInputFeedbackType,
    getTextColorForInputFeedbackType,
  };
}
