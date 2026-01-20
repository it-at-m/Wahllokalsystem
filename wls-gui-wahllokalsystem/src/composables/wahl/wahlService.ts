import type { Wahl } from "@/types/wahl/Wahl.ts";

import { WahlenControllerApi } from "@/api/wls-clients/generated-basisdaten-api";
import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlMapper } from "@/composables/wahl/wahlMapper.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useWahlMapper();
const userNotificationService = useUserNotificationService();

export function useWahlService() {
  const wahlenControllerApi = new WahlenControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  async function getWahlen(
    wahltagID: string,
    sendNotification = true
  ): Promise<Wahl[] | null> {
    const statusStore = useStatusStore();
    try {
      const response = await wahlenControllerApi.getWahlen(wahltagID);
      if (response.status === 200) {
        const wahlen = response.data.map(toModel);
        wahlen.forEach((wahl) => statusStore.addWahl(wahl.wahlID));
        return wahlen;
      } else {
        return null;
      }
    } catch {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim Laden der Wahlen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error("GetWahlen Failed");
    }
  }

  return {
    getWahlen,
  };
}
