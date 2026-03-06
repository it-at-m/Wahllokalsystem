import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";

import {
  Configuration,
  StimmzettelControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelMapper } from "@/composables/experimental/stimmzettelMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelService() {
  const stimmzettelApi = new StimmzettelControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();
  const { toStimmzettelSnapshot, toWaehlerstimmzettelDTO } =
    useStimmzettelMapper();
  const { addNotification } = useUserNotificationService();
  const { logError } = useLogging("useStimmzettelService");

  async function getStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const response = await stimmzettelApi.getStimmzettel(
        wahlID,
        wahlbezirkID
      );
      const responseBody = getNullOn204OrElseResponseData(response);

      let result = null;
      if (responseBody) {
        //TODO es muss nach der Nummer sortiert werden
        result = responseBody.map(toStimmzettelSnapshot);
      }
      addNotification(
        "Stimmzettel erfolgreich geladen",
        UserNotificationCategoryEnum.SUCCESS
      );

      return result;
    } catch (error) {
      logError("getStimmzettel failed", error);
      if (sendNotification) {
        addNotification(
          "Fehler beim Laden der Stimmzettel",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function postStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    stimmzettel: StimmzettelSnapshot[],
    sendNotification = true
  ) {
    try {
      const dtoToSend = stimmzettel.map((stimmzettel, index) =>
        toWaehlerstimmzettelDTO(wahlID, wahlbezirkID, index, stimmzettel)
      );
      await stimmzettelApi.postStimmzettel(wahlID, wahlbezirkID, dtoToSend);
      if (sendNotification) {
        addNotification(
          "Stimmzettel erfolgreich gespeichert",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      logError("postStimmzettel failed", error);
      if (sendNotification) {
        addNotification(
          "Fehler beim Speichern der Stimmzettel",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    getStimmzettel,
    postStimmzettel,
  };
}
