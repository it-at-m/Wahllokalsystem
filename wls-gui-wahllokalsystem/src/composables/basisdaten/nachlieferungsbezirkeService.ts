import {
  Configuration,
  NachlieferungsbezirkeControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useNachlieferungsbezirkeService() {
  const nachlieferungsbezirkeControllerApi =
    new NachlieferungsbezirkeControllerApi(
      new Configuration({
        basePath: BASISDATEN_SERVICE_API_URL,
      })
    );

  const { addNotification } = useUserNotificationService();

  async function loadIsNachlieferungsbezirk(
    wahltagID: string,
    wahlbezirkID: string
  ) {
    try {
      return (
        await nachlieferungsbezirkeControllerApi.isNachlieferungsbezirk(
          wahltagID,
          wahlbezirkID
        )
      ).data;
    } catch (error) {
      addNotification(
        "Fehler beim Laden der Information zu den Nachlieferungsbezirken",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    loadIsNachlieferungsbezirk,
  };
}
