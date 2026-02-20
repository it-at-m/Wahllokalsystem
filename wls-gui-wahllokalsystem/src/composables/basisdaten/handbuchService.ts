import type { AxiosResponse } from "axios";

import { storeToRefs } from "pinia";

import {
  Configuration,
  HandbuchControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useLogging } from "@/composables/common/logging.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { logDebug } = useLogging("handbuchService");

export function useHandbuchService() {
  const handbuchControllerApi = new HandbuchControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  const { addNotification } = useUserNotificationService();
  const { currentUserWahltagID, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());

  async function downloadHandbuch(sendNotification = true) {
    const response = await getHandbuch(sendNotification);
    _downloadPdf(response);
  }

  async function getHandbuch(sendNotification = true) {
    try {
      return await handbuchControllerApi.getHandbuch(
        currentUserWahltagID.value,
        currentUserWahlbezirksArt.value,
        { responseType: "blob" }
      );
    } catch (e) {
      const errorMessage = "Fehler beim Laden des Handbuchs";
      if (sendNotification) {
        addNotification(errorMessage, UserNotificationCategoryEnum.ERROR);
      }
      logDebug(errorMessage, e);
      throw new Error("GetHandbuch Failed");
    }
  }

  function _downloadPdf(response: AxiosResponse) {
    let url: string | null = null;
    try {
      url = window.URL.createObjectURL(
        new Blob([response.data], { type: "application/pdf" })
      );

      const link = document.createElement("a");
      link.href = url;
      link.download = "handbuch.pdf";

      document.body.appendChild(link);
      link.click();
      link.remove();
    } finally {
      if (url) {
        window.URL.revokeObjectURL(url);
      }
    }
  }

  return { downloadHandbuch, getHandbuch };
}
