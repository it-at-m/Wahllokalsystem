export const InputFeedbackTypeEnum = {
  error: "error",
  information: "information",
  success: "success",
};

export type InputFeedbackTypeEnum =
  (typeof InputFeedbackTypeEnum)[keyof typeof InputFeedbackTypeEnum];
