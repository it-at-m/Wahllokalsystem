import type { Status } from "@/types/ergebnismeldung/common/Status.ts";

import {
  Configuration,
  StatusControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStatusMapper } from "@/composables/ergebnismeldung/common/statusMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStatusService() {
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();
  const { addNotification } = useUserNotificationService();
  const { toModel, toDto } = useStatusMapper();

  const statusControllerApi = new StatusControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function getStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    const { setStepDone } = useWorkflowStore();
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
      if (responseData?.schnellmeldung.gedruckt) {
        setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_SCHNELLMELDUNG);
      }
      if (responseData?.niederschrift.gedruckt) {
        setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_NIEDERSCHRIFT);
      }
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

  async function postStatus(
    wahlID: string,
    wahlbezirkID: string,
    status: Status,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
    try {
      await statusControllerApi.setStatus(wahlID, wahlbezirkID, toDto(status));
      if (sendNotification) {
        addNotification(
          `Status für ${wahlname} erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Speichern des Status für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Post Status für ${wahlname} failed.`);
    }
  }

  return {
    getStatus,
    postStatus,
  };
}
