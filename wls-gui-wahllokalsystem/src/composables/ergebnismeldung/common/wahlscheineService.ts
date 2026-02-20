import type { Wahlscheine } from "@/types/ergebnismeldung/common/Wahlscheine.ts";

import {
  Configuration,
  WahlscheineControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useWahlscheineMapper } from "@/composables/ergebnismeldung/common/wahlscheineMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { toModel, toDto } = useWahlscheineMapper();

const { logDebug } = useLogging("wahlscheineService");

export function useWahlscheineService() {
  const ergebnismeldungServiceConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const wahlscheineControllerApi = new WahlscheineControllerApi(
    ergebnismeldungServiceConfiguration
  );

  async function getWahlscheine(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const response = await wahlscheineControllerApi.getWahlscheine(
        wahlID,
        wahlbezirkID
      );
      const responseData = getNullOn204OrElseResponseData(response);
      if (responseData) {
        useWorkflowStore().isAnzahlWahlscheineErfasst = true;
        return toModel(responseData);
      } else {
        return null;
      }
    } catch {
      if (sendNotification) {
        addNotification(
          "Die Wahlscheine konnten nicht geladen werden.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error("Get Wahlscheine Failed");
    }
  }

  async function postWahlscheine(
    wahlID: string,
    wahlbezirkID: string,
    wahlscheine: Wahlscheine
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
    try {
      await wahlscheineControllerApi.postWahlscheine(
        wahlID,
        wahlbezirkID,
        toDto(wahlscheine)
      );
      useWorkflowStore().isAnzahlWahlscheineErfasst = true;
      addNotification(
        `Wahlscheine für ${wahlname} erfolgreich gespeichert`,
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (e) {
      const errorMessage =
        "Fehler beim Speichern der Wahlscheine für " + wahlname;
      logDebug(errorMessage, e);
      addNotification(errorMessage, UserNotificationCategoryEnum.ERROR);
      throw new Error("Post Wahlscheine Failed");
    }
  }

  return {
    getWahlscheine,
    postWahlscheine,
  };
}
