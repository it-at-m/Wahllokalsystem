import { toast } from "vue3-toastify";

import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useUserNotificationService() {
  const toastPosition = toast.POSITION.BOTTOM_LEFT;
  const autoCloseInMs = 5000;

  function addNotification(
    message: string,
    category: UserNotificationCategoryEnum
  ) {
    switch (category) {
      case UserNotificationCategoryEnum.SUCCESS:
        toast.success(message, {
          autoClose: autoCloseInMs,
          position: toastPosition,
        });
        break;
      case UserNotificationCategoryEnum.WARNING:
        toast.warning(message, {
          autoClose: autoCloseInMs,
          position: toastPosition,
        });
        break;
      case UserNotificationCategoryEnum.ERROR:
        toast.error(message, {
          autoClose: false,
          position: toastPosition,
        });
        break;
    }
  }

  return { addNotification };
}
