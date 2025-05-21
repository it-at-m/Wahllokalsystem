import type { KonfigurationDTO } from "@/api/wls-clients/generated-infomanagement-api";
import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import {
  Configuration,
  KonfigurationControllerApi,
} from "@/api/wls-clients/generated-infomanagement-api";
import { useKonfigurationsparameterMapper } from "@/composables/infomanagement/konfigurationsparameterMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { INFOMANAGEMENT_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();
const { toModel } = useKonfigurationsparameterMapper();

export function useKonfigurationsparameterService() {
  const konfigurationControllerApi = new KonfigurationControllerApi(
    new Configuration({
      basePath: INFOMANAGEMENT_SERVICE_API_URL,
    })
  );

  async function getKonfigurationsparameter(
    sendNotification = true
  ): Promise<Konfigurationsparameter[] | null> {
    try {
      const response = await konfigurationControllerApi.getKonfigurations();
      return toModel(response.data as KonfigurationDTO[]); // todo: ist hier beim client der rückgabetyp falsch, dass da ein object statt dto kommt?
    } catch {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim laden der Konfigurationsparameter",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error("GetKonfigurations Failed");
    }
  }

  return { getKonfigurationsparameter };
}
