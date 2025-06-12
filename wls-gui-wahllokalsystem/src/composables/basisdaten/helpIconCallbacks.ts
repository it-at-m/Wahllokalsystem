import { useTestDruck } from "@/composables/basisdaten/testDruck.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { TEAMVIEWER_URL, WAHLRAUMFINDER_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useHelpIconCallbacks() {
  const { buildTemplate } = useTestDruck();
  const { addNotification } = useUserNotificationService();

  function openWahlraumfinder() {
    const win = window.open(WAHLRAUMFINDER_URL, "_blank");
    if (win) {
      win.focus();
    }
  }

  function startFernzugriff() {
    const win = window.open(TEAMVIEWER_URL, "_blank");
    if (win) {
      win.focus();
    }
  }

  function printTestdruck() {
    const printWindow = window.open(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );

    if (printWindow) {
      printWindow.document.body.innerHTML = buildTemplate();
      printWindow.print();
      printWindow.close();
    } else {
      addNotification(
        "Druck-Popup blockiert. Bitte erlauben Sie alle Popups für diese Seite",
        UserNotificationCategoryEnum.WARNING
      );
    }
  }

  return { openWahlraumfinder, startFernzugriff, printTestdruck };
}
