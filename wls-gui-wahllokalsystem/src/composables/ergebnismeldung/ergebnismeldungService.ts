import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { StimmzettelumschlaegeControllerApi } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useErgebnismeldungMapper } from "@/composables/ergebnismeldung/ergebnismeldungMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toDto } = useErgebnismeldungMapper();
const userNotificationService = useUserNotificationService();

export function useErgebnismeldungService() {
  const ergebnismeldungControllerApi = new StimmzettelumschlaegeControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );

  async function saveStimmzettelumschlaege(
    wahlID: string,
    wahlbezirkID: string,
    stimmzettelumschlaege: Stimmzettelumschlaege,
    sendNotification = true
  ): Promise<void> {
    try {
      await ergebnismeldungControllerApi.postStimmzettelumschlaege(
        wahlID,
        wahlbezirkID,
        toDto(stimmzettelumschlaege)
      );
      userNotificationService.addNotification(
        "Stimmzettelumschlaege erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Speichern der Stimmzettelumschlaege fehlgeschlagen.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    saveStimmzettelumschlaege,
  };
}
