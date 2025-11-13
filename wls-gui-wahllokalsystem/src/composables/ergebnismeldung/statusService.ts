import {
  Configuration,
  StatusControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStatusMapper } from "@/composables/ergebnismeldung/statusMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStatusService() {
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();
  const { addNotification } = useUserNotificationService();
  const { toModel } = useStatusMapper();

  const statusControllerApi = new StatusControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function getStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    try {
      const response = await statusControllerApi.getStatus(
        wahlID,
        wahlbezirkID
      );

      if (sendNotification) {
        const wahlname =
          wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
        addNotification(
          `Status für ${wahlname} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : null;
    } catch {
      const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
      if (sendNotification) {
        addNotification(
          `Fehler beim Laden des Status für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get Status für ${wahlname} failed.`);
    }
  }

  return {
    getStatus,
  };
}
