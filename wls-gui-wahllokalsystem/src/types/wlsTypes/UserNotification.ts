import type { UserNotificationTypeEnum } from "@/types/wlsTypes/UserNotificationTypeEnum.ts";

export interface UserNotification {
  id: string;
  message: string;
  category: UserNotificationTypeEnum;
}
