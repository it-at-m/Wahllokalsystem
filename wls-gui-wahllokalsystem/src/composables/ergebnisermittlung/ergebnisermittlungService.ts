import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import {
  Configuration,
  StimmzettelumschlaegeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toDto } = useErgebnisermittlungMapper();
const { addNotification } = useUserNotificationService();

export function useErgebnisermittlungService() {
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
        toDto(stimmzettelumschlaege, wahlID, wahlbezirkID)
      );
      if (sendNotification) {
        addNotification(
          "Stimmzettelumschlaege erfolgreich gespeichert.",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
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
