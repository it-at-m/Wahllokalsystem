import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

import { Configuration } from "@/api/wls-clients/generated-auth-api";
import { StimmabgabevermerkeControllerApi } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmabgabevermerkeMapper } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { toModel } = useStimmabgabevermerkeMapper();

export function useStimmabgabevermerkeService() {
  const stimmabgabevermerkeControllerApi = new StimmabgabevermerkeControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );

  async function getStimmabgabevermerke(
    wahlbezirkID: string,
    waehlerverzeichnisNummer: number,
    sendNotification = true
  ): Promise<Stimmabgabevermerke> {
    try {
      const response =
        await stimmabgabevermerkeControllerApi.getStimmabgabevermerke(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
      return toModel(response.data);
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

  return { getStimmabgabevermerke };
}
