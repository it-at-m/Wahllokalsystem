import { KopfdatenControllerApi } from "@/api/wls-clients/generated-basisdaten-api";
import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
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

  async function getKopfdaten(wahlID: string, wahlbezirk: string) {
    try {
      const response = await kopfdatenControllerApi.getKopfdaten(
        wahlID,
        wahlbezirk
      );
      if (response.status === 200) {
        return toModel(response.data);
      } else {
        return null;
      }
    } catch {
      userNotificationService.addNotification(
        "Fehler beim laden der Kopfdaten",
        UserNotificationCategoryEnum.ERROR
      );
      throw new Error("GetKopfdaten Failed");
    }
  }

  return {
    getKopfdaten,
  };
}
