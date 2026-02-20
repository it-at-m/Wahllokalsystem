export const UserNotificationCategoryEnum = {
  SUCCESS: "Success",
  WARNING: "Warning",
  ERROR: "Error",
} as const;

export type UserNotificationCategoryEnum =
  (typeof UserNotificationCategoryEnum)[keyof typeof UserNotificationCategoryEnum];
