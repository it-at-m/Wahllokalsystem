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
      case InputFeedbackTypeEnum.warning:
        return "$alert";
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
      case InputFeedbackTypeEnum.warning:
        return "warning";
    }
  }

  function getBackgroundColorAndBoldTextForInputFeedbackType(
    inputFeedbackType: InputFeedbackTypeEnum
  ) {
    switch (inputFeedbackType) {
      case InputFeedbackTypeEnum.error:
        return "bg-error font-weight-bold";
      case InputFeedbackTypeEnum.information:
        return "bg-info font-weight-bold";
      case InputFeedbackTypeEnum.success:
        return "bg-success font-weight-bold";
      case InputFeedbackTypeEnum.warning:
        return "bg-warning font-weight-bold";
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
      case InputFeedbackTypeEnum.warning:
        return "border-warning";
    }
  }

  return {
    getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType,
    getIconForInputFeedbackType,
    getBackgroundColorAndBoldTextForInputFeedbackType,
  };
}
