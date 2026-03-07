export const StimmzettelEventTypeEnum = {
  SAVE: "SAVE",
} as const;

export type StimmzettelEventTypeEnum =
  (typeof StimmzettelEventTypeEnum)[keyof typeof StimmzettelEventTypeEnum];
