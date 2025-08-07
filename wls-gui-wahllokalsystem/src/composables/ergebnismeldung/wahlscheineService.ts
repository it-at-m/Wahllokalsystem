import { Configuration } from "@/api/wls-clients/generated-briefwahl-api";
import { WahlscheineControllerApi } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useWahlscheineMapper } from "@/composables/ergebnismeldung/wahlscheineMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { toModel } = useWahlscheineMapper();

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
      return responseData ? toModel(responseData) : null;
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

  return {
    getWahlscheine,
  };
}
