export const InputFeedbackTypeEnum = {
  error: "error",
  information: "information",
};

export type InputFeedbackTypeEnum =
  (typeof InputFeedbackTypeEnum)[keyof typeof InputFeedbackTypeEnum];
