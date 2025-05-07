import type { Wahl } from "@/types/wahl/Wahl.ts";

import { WahlenControllerApi } from "@/api/wls-clients/generated-basisdaten-api";
import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlMapper } from "@/composables/wahl/wahlMapper.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useWahlMapper();
const userNotficicationService = useUserNotificationService();

export function useWahlService() {
  const wahlenControllerApi = new WahlenControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  function loadWahlen(
    wahltagID: string,
    sendNotification: boolean
  ): Promise<Wahl[]> {
    try {
      return wahlenControllerApi
        .getWahlen(wahltagID)
        .then((response) => response.data.map(toModel));
    } catch (error) {
      if (sendNotification) {
        userNotficicationService.addNotification(
          "Fehler beim laden der Wahlen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    loadWahlen,
  };
}
