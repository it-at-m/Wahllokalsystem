import type { UserNotification } from "@/types/wlsTypes/UserNotification.ts";
import type { UserNotificationTypeEnum } from "@/types/wlsTypes/UserNotificationTypeEnum.ts";

import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid";

export const useUserNotificationStore = defineStore("notification", {
  state: () => ({
    userNotifications: [] as UserNotification[],
  }),

  actions: {
    addNotification(message: string, category: UserNotificationTypeEnum) {
      const id = uuidv4();
      const newUserNotification: UserNotification = {
        message,
        category,
        id,
      };
      this.userNotifications.push(newUserNotification);
    },

    removeNotification(id: string) {
      this.userNotifications = this.userNotifications.filter(
        (userNotification) => userNotification.id !== id
      );
    },
  },
});
