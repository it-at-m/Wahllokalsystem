import {
  AWerteControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useAWerteMapper } from "@/composables/ergebnismeldung/aWerteMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useAWerteMapper();
const { addNotification } = useUserNotificationService();

export function useAWerteService() {
  const aWerteController = new AWerteControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function getAWerte(wahlbezirkId: string, sendNotification = true) {
    try {
      const response = await aWerteController.getAWerte(wahlbezirkId);
      if (sendNotification) {
        addNotification(
          `AWerte erfolgreich geladen`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      return response.data.map((aWerteDto) => toModel(aWerteDto));
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Laden der AWerte`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get AWerte failed for wahlbezirkId: ${wahlbezirkId}`);
    }
  }

  return {
    getAWerte,
  };
}
