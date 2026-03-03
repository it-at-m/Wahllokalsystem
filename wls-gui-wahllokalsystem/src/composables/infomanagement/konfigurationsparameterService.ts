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
  ): Promise<Konfigurationsparameter[]> {
    try {
      const response = await konfigurationControllerApi.getKonfigurations();
      return toModel(response.data);
    } catch {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim Laden der Konfigurationsparameter",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error("GetKonfigurations Failed");
    }
  }

  return { getKonfigurationsparameter };
}
