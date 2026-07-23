import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungStatus.ts";

import {
  Configuration,
  StimmzettelerfassungControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStimmzettelerfassungStatusMapper } from "@/composables/dse/stimmzettelerfassungStatusMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { dtoToModel } = useStimmzettelerfassungStatusMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useDseWorkflowStatusService() {
  const stimmzettelerfassungControllerApi =
    new StimmzettelerfassungControllerApi(
      new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
    );

  async function loadDseWorkflowStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<StimmzettelerfassungStatus | null> {
    try {
      const response =
        await stimmzettelerfassungControllerApi.getStimmzettelerfassungStatus(
          wahlID,
          wahlbezirkID
        );

      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? dtoToModel(responseData) : null;
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Abrufen des StimmzettelerfassungStatus ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    loadDseWorkflowStatus,
  };
}
