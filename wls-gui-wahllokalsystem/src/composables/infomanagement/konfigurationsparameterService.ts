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
  ): Promise<Konfigurationsparameter[]> {
    try {
      const response = await konfigurationControllerApi.getKonfigurations();
      // todo: mit issue #1328 wird der rückgabetyp des requests angepasst und das `as KonfigurationDTO[]` kann entfernt werden
      return toModel(response.data as KonfigurationDTO[]);
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
