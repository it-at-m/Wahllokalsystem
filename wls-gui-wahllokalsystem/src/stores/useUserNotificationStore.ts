import type { UserNotification } from "@/types/wlsTypes/UserNotification.ts";
import type { UserNotificationTypeEnum } from "@/types/wlsTypes/UserNotificationTypeEnum.ts";

import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid";

export const useUserNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [] as UserNotification[],
  }),

  actions: {
    addNotification(message: string, category: UserNotificationTypeEnum) {
      const id = uuidv4();
      const newNotification: UserNotification = {
        message,
        category,
        id,
      };
      this.notifications.push(newNotification);
    },

    removeNotification(id: string) {
      this.notifications = this.notifications.filter(
        (notification) => notification.id !== id
      );
    },
  },
});
