import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";

import {
  AWerteControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useAWerteMapper } from "@/composables/ergebnismeldung/common/aWerteMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useAWerteMapper();
const { addNotification } = useUserNotificationService();
const { axiosConfigWrapper } = useCommonApiUtils();

export function useAWerteService() {
  const aWerteController = new AWerteControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function getAWerte(wahlbezirkId: string, sendNotification = true) {
    try {
      const response = await aWerteController.getAWerte(
        wahlbezirkId,
        axiosConfigWrapper().requestAsOnlineFirst()
      );
      if (sendNotification) {
        addNotification(
          `AWerte erfolgreich geladen`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      return response.data.map((aWerteDto) => toModel(aWerteDto));
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Laden der AWerte`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get AWerte failed for wahlbezirkId: ${wahlbezirkId}`);
    }
  }

  async function getAWerteForWahlbezirkAndWahl(
    wahlbezirkID: string,
    wahlID: string
  ): Promise<AWerte> {
    let aWerte;
    try {
      aWerte = await getAWerte(wahlbezirkID, false);
    } catch {
      throw new Error(`Fehler beim Laden der AWerte`);
    }

    const filteredAWert = aWerte.find(
      ({ bezirkUndWahlID }) => bezirkUndWahlID.wahlID === wahlID
    );

    if (!filteredAWert) {
      throw new Error(`Kein AWert gefunden für wahlID: ${wahlID}`);
    }
    return filteredAWert;
  }

  return {
    getAWerte,
    getAWerteForWahlbezirkAndWahl,
  };
}
