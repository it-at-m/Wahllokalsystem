import {
  Configuration,
  KopfdatenControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useKopfdatenMapper } from "@/composables/kopfdaten/kopfdatenMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useKopfdatenMapper();

const userNotificationService = useUserNotificationService();

export function useKopfdatenService() {
  const kopfdatenControllerApi = new KopfdatenControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  async function getKopfdaten(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const response = await kopfdatenControllerApi.getKopfdaten(
        wahlID,
        wahlbezirkID
      );
      return toModel(response.data);
    } catch {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim Laden der Kopfdaten",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error("GetKopfdaten Failed");
    }
  }

  return {
    getKopfdaten,
  };
}
