import { randomUUID } from "node:crypto";

import type { UserNotification } from "@/types/wlsTypes/UserNotification.ts";
import type { UserNotificationTypeEnum } from "@/types/wlsTypes/UserNotificationTypeEnum.ts";

import { defineStore } from "pinia";

export const useUserNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [] as UserNotification[],
  }),

  actions: {
    addNotification(message: string, category: UserNotificationTypeEnum) {
      const id = randomUUID();
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
