import {
  Configuration,
  StimmabgabevermerkeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useStimmabgabevermerkeMapper } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { toModel } = useStimmabgabevermerkeMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

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
  ) {
    try {
      const response =
        await stimmabgabevermerkeControllerApi.getStimmabgabevermerke(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : null;
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
