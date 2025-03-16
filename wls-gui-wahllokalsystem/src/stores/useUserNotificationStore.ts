import type { UserNotification } from "@/types/wlsTypes/UserNotification.ts";

import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid";
import { toast } from "vue3-toastify";

import { UserNotificationCategoryEnum } from "@/types/wlsTypes/UserNotificationCategoryEnum.ts";

export const useUserNotificationStore = defineStore("notification", {
  state: () => ({
    userNotifications: [] as UserNotification[],
  }),

  actions: {
    addNotification(message: string, category: UserNotificationCategoryEnum) {
      const id = uuidv4();
      const newUserNotification: UserNotification = {
        message,
        category,
        id,
      };
      this.userNotifications.push(newUserNotification);
      switch (category) {
        case UserNotificationCategoryEnum.ERFOLG:
          toast.success(message, {
            autoClose: 5000,
            toastId: id,
            position: toast.POSITION.BOTTOM_LEFT,
            onClose: () => this.removeNotification(id),
          });
          break;
        case UserNotificationCategoryEnum.WARNUNG:
          toast.warning(message, {
            autoClose: 5000,
            toastId: id,
            position: toast.POSITION.BOTTOM_LEFT,
            onClose: () => this.removeNotification(id),
          });
          break;
        case UserNotificationCategoryEnum.FEHLER:
          toast.error(message, {
            autoClose: false,
            toastId: id,
            position: toast.POSITION.BOTTOM_LEFT,
            onClose: () => this.removeNotification(id),
          });
          break;
      }
    },

    removeNotification(id: string) {
      this.userNotifications = this.userNotifications.filter(
        (userNotification) => userNotification.id !== id
      );
    },
  },
});
