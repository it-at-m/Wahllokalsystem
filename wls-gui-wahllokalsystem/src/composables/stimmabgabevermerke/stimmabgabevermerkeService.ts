import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

import {
  Configuration,
  StimmabgabevermerkeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useStimmabgabevermerkeMapper } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { toModel, toDto } = useStimmabgabevermerkeMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

const { logDebug } = useLogging("stimmabgabevermerkeService");

export function useStimmabgabevermerkeService() {
  const stimmabgabevermerkeControllerApi = new StimmabgabevermerkeControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );

  async function getStimmabgabevermerke(
    wahlbezirkID: string,
    wahlID: string,
    waehlerverzeichnisNummer: number,
    sendNotification = true
  ) {
    try {
      const response =
        await stimmabgabevermerkeControllerApi.getStimmabgabevermerke(
          wahlbezirkID,
          wahlID,
          waehlerverzeichnisNummer
        );
      const responseData = getNullOn204OrElseResponseData(response);
      if (responseData) {
        useWorkflowStore().isStimmabgabevermerkeErfasst = true;
        return toModel(responseData);
      } else {
        return null;
      }
    } catch (e) {
      if (sendNotification) {
        addNotification(
          "Fehler beim Laden der Stimmabgabevermerke.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw e;
    }
  }

  async function postStimmabgabevermerke(
    wahlbezirkID: string,
    wahlID: string,
    waehlerverzeichnisNummer: number,
    stimmabgabevermerke: Stimmabgabevermerke
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname =
      wahlenActions.getWahlNameOrBlankStringById(stimmabgabevermerke.wahlID) ||
      "";
    try {
      await stimmabgabevermerkeControllerApi.postStimmabgabevermerke(
        wahlbezirkID,
        wahlID,
        waehlerverzeichnisNummer,
        toDto(stimmabgabevermerke)
      );
      useWorkflowStore().isStimmabgabevermerkeErfasst = true;
      addNotification(
        `Stimmabgabevermerke für ${wahlname} erfolgreich gespeichert`,
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (e) {
      const errorMessage =
        "Fehler beim Speichern der Stimmabgabevermerke für " + wahlname;
      logDebug(errorMessage, e);
      addNotification(errorMessage, UserNotificationCategoryEnum.ERROR);
      throw new Error("Post Stimmabgabevermerke Failed", { cause: e });
    }
  }

  return { getStimmabgabevermerke, postStimmabgabevermerke };
}
