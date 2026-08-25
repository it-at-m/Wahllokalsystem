import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";

import { readonly, ref } from "vue";

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
const { dtoToModel, modelToDto } = useStimmzettelerfassungStatusMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useDseWorkflowStatusService() {
  const stimmzettelerfassungControllerApi =
    new StimmzettelerfassungControllerApi(
      new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
    );

  const isWorkflowStatusLoading = ref(false);

  async function loadDseWorkflowStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<StimmzettelerfassungStatus | null> {
    isWorkflowStatusLoading.value = true;
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
    } finally {
      isWorkflowStatusLoading.value = false;
    }
  }

  async function saveDseWorkflowStatus(
    wahlID: string,
    wahlbezirkID: string,
    status: StimmzettelerfassungStatus,
    sendNotification = true
  ) {
    try {
      const dtoToSend = modelToDto(status);
      await stimmzettelerfassungControllerApi.saveStimmzettelerfassungStatus(
        wahlID,
        wahlbezirkID,
        dtoToSend
      );
      if (sendNotification) {
        addNotification(
          "Speichern des Workflow-Status erfolgreich",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Speichern des Workflow-Status ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    isWorkflowStatusLoading: readonly(isWorkflowStatusLoading),
    loadDseWorkflowStatus,
    saveDseWorkflowStatus,
  };
}
