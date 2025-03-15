import type { UserNotificationCategoryEnum } from "@/types/wlsTypes/UserNotificationCategoryEnum.ts";

export interface UserNotification {
  id: string;
  message: string;
  category: UserNotificationCategoryEnum;
}
