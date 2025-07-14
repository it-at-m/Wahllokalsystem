import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten";

import {
  Configuration,
  WahlbriefdatenControllerApi,
} from "@/api/wls-clients/generated-briefwahl-api";
import { useBriefwahlMapper } from "@/composables/briefwahl/briefwahlMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BRIEFWAHL_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();

const { toWahlbriefdatenModel, toWahlbriefdatenWriteDTO } =
  useBriefwahlMapper();

export function useBriefwahlService() {
  const briefwahlServiceConfiguration = new Configuration({
    basePath: BRIEFWAHL_SERVICE_API_URL,
  });
  const wahlbriefdatenControllerApi = new WahlbriefdatenControllerApi(
    briefwahlServiceConfiguration
  );

  async function getWahlbriefdaten(
    wahlbezirkID: string
  ): Promise<Wahlbriefdaten> {
    try {
      return await wahlbriefdatenControllerApi
        .getWahlbriefdaten(wahlbezirkID)
        .then((response) => toWahlbriefdatenModel(response.data));
    } catch (error) {
      userNotificationService.addNotification(
        "Fehler beim Laden der Wahlbriefdaten.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postWahlbriefdaten(
    wahlbezirkID: string,
    wahlbriefdaten: Wahlbriefdaten
  ): Promise<void> {
    const wahlbriefdatenWriteDTO = toWahlbriefdatenWriteDTO(wahlbriefdaten);

    try {
      await wahlbriefdatenControllerApi.postWahlbriefdaten(
        wahlbezirkID,
        wahlbriefdatenWriteDTO
      );
      userNotificationService.addNotification(
        "Wahlbriefdaten erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Wahlbriefdaten fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    getWahlbriefdaten,
    postWahlbriefdaten,
  };
}
