import { storeToRefs } from "pinia";

import { useTestDruck } from "@/composables/basisdaten/testDruck.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { TEAMVIEWER_URL } from "@/constants.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useHelpIconCallbacks() {
  const { buildTemplate } = useTestDruck();
  const { addNotification } = useUserNotificationService();
  const { waehlerverzeichnisUrl, wahlraumUrl } = storeToRefs(
    useInfomanagementStore()
  );

  function openWahlraumfinder() {
    if (wahlraumUrl.value) {
      const win = window.open(wahlraumUrl.value, "_blank");
      if (win) {
        win.focus();
      }
    }
  }

  function openWaehlerverzeichnis() {
    if (waehlerverzeichnisUrl.value) {
      const win = window.open(waehlerverzeichnisUrl.value, "_blank");
      if (win) {
        win.focus();
      }
    }
  }

  function isWaehlerverzeichnisUrlAvailable(): boolean {
    return !!waehlerverzeichnisUrl.value;
  }

  function isWahlraumfinderUrlAvailable(): boolean {
    return !!wahlraumUrl.value;
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

  return {
    openWahlraumfinder,
    openWaehlerverzeichnis,
    isWaehlerverzeichnisUrlAvailable,
    isWahlraumfinderUrlAvailable,
    startFernzugriff,
    printTestdruck,
  };
}
