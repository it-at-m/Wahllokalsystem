import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import {
  Configuration,
  WaehleranzahlControllerApi,
} from "@/api/wls-clients/generated-monitoring-api";
import { useWahlbeteiligungMapper } from "@/composables/monitoring/wahlbeteiligungMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { MONITORING_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();
const { toDto, toModel } = useWahlbeteiligungMapper();

export function useMonitoringService() {
  const waehlerAnzahlControllerApi = new WaehleranzahlControllerApi(
    new Configuration({ basePath: MONITORING_SERVICE_API_URL })
  );

  async function getWahlbeteiligung(wahlID: string, wahlbezirkID: string) {
    return await waehlerAnzahlControllerApi
      .getWahlbeteiligung(wahlID, wahlbezirkID)
      .then((response) => toModel(response.data));
  }

  async function postWahlbeteiligung(
    wahlbezirkID: string,
    wahlID: string,
    waehleranzahl: Waehleranzahl
  ): Promise<void> {
    const waehleranzahlDTO = toDto(waehleranzahl);

    try {
      await waehlerAnzahlControllerApi.postWahlbeteiligung(
        wahlbezirkID,
        wahlID,
        waehleranzahlDTO
      );
    } catch {
      userNotificationService.addNotification(
        "Fehler beim Übermitteln der Wahlbeteiligung.",
        UserNotificationCategoryEnum.ERROR
      );
    }
  }

  return { getWahlbeteiligung, postWahlbeteiligung };
}
