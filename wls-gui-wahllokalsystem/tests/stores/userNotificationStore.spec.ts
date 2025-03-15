import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, test } from "vitest";

import { useUserNotificationStore } from "@/stores/useUserNotificationStore.ts";
import { UserNotificationTypeEnum } from "@/types/wlsTypes/UserNotificationTypeEnum.ts";

describe("userNotificationStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useUserNotificationStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useUserNotificationStore();
  });

  describe("addNotification", () => {
    test("should_addANewNotification_when_called", () => {
      const category = UserNotificationTypeEnum.ERFOLG;
      const message = "Test message";
      expect(unitUnderTest.notifications.length).toStrictEqual(0);

      unitUnderTest.addNotification(message, category);

      expect(unitUnderTest.notifications.length).toStrictEqual(1);
      expect(unitUnderTest.notifications[0]).toStrictEqual(
        expect.objectContaining({
          message: message,
          category: category,
          id: expect.any(String),
        })
      );
    });
  });

  describe("removeNotification", () => {
    test("should_removeNotificationById_when_calledWithId", () => {
      unitUnderTest.addNotification(
        "Test message One",
        UserNotificationTypeEnum.ERFOLG
      );
      unitUnderTest.addNotification(
        "Test message Two",
        UserNotificationTypeEnum.ERFOLG
      );
      unitUnderTest.addNotification(
        "Test message Three",
        UserNotificationTypeEnum.ERFOLG
      );

      const notificationToDeleteId = unitUnderTest.notifications[1].id;

      unitUnderTest.removeNotification(notificationToDeleteId);

      const notificationExists = unitUnderTest.notifications.some(
        (notification) => notification.id === notificationToDeleteId
      );

      expect(unitUnderTest.notifications.length).toStrictEqual(2);
      expect(notificationExists).toStrictEqual(false);
    });
  });
});
