export const InputFeedbackTypeEnum = {
  error: "error",
  information: "information",
  success: "success",
  warning: "warning",
};

export type InputFeedbackTypeEnum =
  (typeof InputFeedbackTypeEnum)[keyof typeof InputFeedbackTypeEnum];
