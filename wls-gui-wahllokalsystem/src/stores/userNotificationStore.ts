import type { UserNotification } from "@/types/userNotification/UserNotification.ts";
import type { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid";
import { ref } from "vue";

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
  }

  function removeNotification(id: string) {
    userNotifications.value = userNotifications.value.filter(
      (userNotification) => userNotification.id !== id
    );
  }

  return { userNotifications, addNotification, removeNotification };
});
