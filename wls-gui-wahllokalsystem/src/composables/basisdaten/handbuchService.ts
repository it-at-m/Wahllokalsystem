import type { AxiosResponse } from "axios";

import { storeToRefs } from "pinia";

import {
  Configuration,
  HandbuchControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useHandbuchService() {
  const handbuchControllerApi = new HandbuchControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  const { addNotification } = useUserNotificationService();
  const { currentUserWahltagID, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());

  async function getHandbuch(sendNotification = true) {
    if (currentUserWahltagID.value) {
      try {
        const response = await handbuchControllerApi.getHandbuch(
          currentUserWahltagID.value,
          currentUserWahlbezirksArt.value,
          { responseType: "blob" }
        );

        _downloadPdf(response);
      } catch {
        if (sendNotification) {
          addNotification(
            "Fehler beim laden des Handbuchs",
            UserNotificationCategoryEnum.ERROR
          );
        }
        throw new Error("GetHandbuch Failed");
      }
    }
  }

  function _downloadPdf(response: AxiosResponse) {
    // Erstelle eine URL für den Blob
    const url = window.URL.createObjectURL(
      new Blob([response.data], { type: "application/pdf" })
    );

    // Erstelle ein Link-Element und simuliere einen Klick darauf
    const link = document.createElement("a");
    link.href = url;
    link.download = "handbuch.pdf";

    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }

  return { getHandbuch };
}
