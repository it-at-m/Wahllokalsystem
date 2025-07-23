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
      case InputFeedbackTypeEnum.success:
        return "$valid";
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
      case InputFeedbackTypeEnum.success:
        return "success";
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
      case InputFeedbackTypeEnum.success:
        return "text-success";
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
      case InputFeedbackTypeEnum.success:
        return "border-success";
    }
  }

  return {
    getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType,
    getIconForInputFeedbackType,
    getTextColorForInputFeedbackType,
  };
}
