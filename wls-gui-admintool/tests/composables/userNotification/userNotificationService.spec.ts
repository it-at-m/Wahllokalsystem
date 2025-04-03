import { afterEach, describe, expect, it, vi } from "vitest";
import { toast } from "vue3-toastify";

import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

vi.mock("vue3-toastify", () => ({
  toast: {
    success: vi.fn(),
    warning: vi.fn(),
    error: vi.fn(),
    POSITION: {
      BOTTOM_LEFT: "bottom-left",
    },
  },
}));

describe("useUserNotificationService.ts", () => {
  const notificationService = useUserNotificationService();

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("should_callToastSuccess_when_calledWithSuccessCategory", () => {
    const message = "Success";
    notificationService.addNotification(
      message,
      UserNotificationCategoryEnum.SUCCESS
    );

    expect(toast.success).toHaveBeenCalledWith(message, {
      autoClose: 5000,
      position: toast.POSITION.BOTTOM_LEFT,
    });
  });

  it("should_callToastWarning_when_calledWithWarningCategory", () => {
    const message = "Warning";
    notificationService.addNotification(
      message,
      UserNotificationCategoryEnum.WARNING
    );

    expect(toast.warning).toHaveBeenCalledWith(message, {
      autoClose: 5000,
      position: toast.POSITION.BOTTOM_LEFT,
    });
  });

  it("should_callToastError_when_calledWithErrorCategory", () => {
    const message = "Error";
    notificationService.addNotification(
      message,
      UserNotificationCategoryEnum.ERROR
    );

    expect(toast.error).toHaveBeenCalledWith(message, {
      autoClose: false,
      position: toast.POSITION.BOTTOM_LEFT,
    });
  });

  it("should_notCallAnyToast_when_calledWithUnknownCategory", () => {
    const message = "Unknown";
    // @ts-expect-error pass unknown category to the service
    notificationService.addNotification(message, "UNKNOWN_CATEGORY");

    expect(toast.success).not.toHaveBeenCalled();
    expect(toast.warning).not.toHaveBeenCalled();
    expect(toast.error).not.toHaveBeenCalled();
  });
});
