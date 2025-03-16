import type { UserNotification } from "@/types/wlsTypes/UserNotification.ts";

import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid";
import { ref } from "vue";
import { toast } from "vue3-toastify";

import { UserNotificationCategoryEnum } from "@/types/wlsTypes/UserNotificationCategoryEnum.ts";

export const useUserNotificationStore = defineStore("userNotification", () => {
  const userNotifications = ref([] as UserNotification[]);

  function addNotification(
    message: string,
    category: UserNotificationCategoryEnum
  ) {
    const id = uuidv4();
    const newUserNotification: UserNotification = {
      message,
      category,
      id,
    };
    userNotifications.value.push(newUserNotification);
    displayToast(newUserNotification);
  }

  function displayToast(newUserNotification: UserNotification) {
    const toastPosition = toast.POSITION.BOTTOM_LEFT;
    switch (newUserNotification.category) {
      case UserNotificationCategoryEnum.ERFOLG:
        toast.success(newUserNotification.message, {
          autoClose: 5000,
          toastId: newUserNotification.id,
          position: toastPosition,
          onClose: () => removeNotification(newUserNotification.id),
        });
        break;
      case UserNotificationCategoryEnum.WARNUNG:
        toast.warning(newUserNotification.message, {
          autoClose: 5000,
          toastId: newUserNotification.id,
          position: toastPosition,
          onClose: () => removeNotification(newUserNotification.id),
        });
        break;
      case UserNotificationCategoryEnum.FEHLER:
        toast.error(newUserNotification.message, {
          autoClose: false,
          toastId: newUserNotification.id,
          position: toastPosition,
          onClose: () => removeNotification(newUserNotification.id),
        });
        break;
    }
  }

  function removeNotification(id: string) {
    userNotifications.value = userNotifications.value.filter(
      (userNotification) => userNotification.id !== id
    );
  }

  return { userNotifications, addNotification, removeNotification };
});
