import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, test } from "vitest";

import { useUserNotificationStore } from "@/stores/userNotificationStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

describe("userNotificationStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useUserNotificationStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useUserNotificationStore();
  });

  describe("addNotification", () => {
    test("should_addANewNotification_when_called", () => {
      const category = UserNotificationCategoryEnum.SUCCESS;
      const message = "Test message";
      expect(unitUnderTest.userNotifications.length).toStrictEqual(0);

      unitUnderTest.addNotification(message, category);

      expect(unitUnderTest.userNotifications.length).toStrictEqual(1);
      expect(unitUnderTest.userNotifications[0]).toStrictEqual(
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
        UserNotificationCategoryEnum.SUCCESS
      );
      unitUnderTest.addNotification(
        "Test message Two",
        UserNotificationCategoryEnum.SUCCESS
      );
      unitUnderTest.addNotification(
        "Test message Three",
        UserNotificationCategoryEnum.SUCCESS
      );

      const notificationToDeleteId = unitUnderTest.userNotifications[1].id;

      unitUnderTest.removeNotification(notificationToDeleteId);

      const notificationExists = unitUnderTest.userNotifications.some(
        (notification) => notification.id === notificationToDeleteId
      );

      expect(unitUnderTest.userNotifications.length).toStrictEqual(2);
      expect(notificationExists).toStrictEqual(false);
    });
  });
});
