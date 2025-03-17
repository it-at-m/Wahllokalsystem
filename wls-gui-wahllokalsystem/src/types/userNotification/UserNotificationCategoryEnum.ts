export const UserNotificationCategoryEnum = {
  ERFOLG: "Erfolg",
  WARNUNG: "Warnung",
  FEHLER: "Fehler",
} as const;

export type UserNotificationCategoryEnum =
  (typeof UserNotificationCategoryEnum)[keyof typeof UserNotificationCategoryEnum];
