import { storeToRefs } from "pinia";

import { useTestDruck } from "@/composables/basisdaten/testDruck.ts";
import { usePrintUtils } from "@/composables/ergebnismeldung/MBW/printUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { TEAMVIEWER_URL } from "@/constants.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useHelpIconCallbacks() {
  const { buildTemplate } = useTestDruck();
  const { printInWindow } = usePrintUtils();
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

  async function printTestdruck() {
    if (!(await printInWindow(buildTemplate()))) {
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
