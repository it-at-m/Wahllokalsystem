import { storeToRefs } from "pinia";

import { useTestDruck } from "@/composables/basisdaten/testDruck.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { TEAMVIEWER_URL } from "@/constants.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useHelpIconCallbacks() {
  const { buildTemplate } = useTestDruck();
  const { addNotification } = useUserNotificationService();
  const { konfigurationsparameter } = storeToRefs(useInfomanagementStore());

  function openWahlraumfinder() {
    if (konfigurationsparameter.value) {
      const wahllokalfinderParam = konfigurationsparameter.value.find(
        (param) => param.schluessel === "WAHLLOKALFINDER_URL"
      );
      if (wahllokalfinderParam) {
        const win = window.open(wahllokalfinderParam.wert, "_blank");
        if (win) {
          win.focus();
        }
      }
    }
  }

  function openWaehlerverzeichnis() {
    if (konfigurationsparameter.value) {
      const waehlerverzeichnisParam = konfigurationsparameter.value.find(
        (param) => param.schluessel === "WAEHLERVERZEICHNIS_URL"
      );
      if (waehlerverzeichnisParam) {
        const win = window.open(waehlerverzeichnisParam.wert, "_blank");
        if (win) {
          win.focus();
        }
      }
    }
  }

  function waehlerverzeichnisAvailable(): boolean {
    if (konfigurationsparameter.value) {
      const waehlerverzeichnisParam = konfigurationsparameter.value.find(
        (param) => param.schluessel === "WAEHLERVERZEICHNIS_URL"
      );
      return !!waehlerverzeichnisParam && !!waehlerverzeichnisParam.wert;
    }
    return false;
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
    callbackWaehlerverzeichnisAvailable: waehlerverzeichnisAvailable,
    startFernzugriff,
    printTestdruck,
  };
}
