import type { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export interface UserNotification {
  id: string;
  message: string;
  category: UserNotificationCategoryEnum;
}
