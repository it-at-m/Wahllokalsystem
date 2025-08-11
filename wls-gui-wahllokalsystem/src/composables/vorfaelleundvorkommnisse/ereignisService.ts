import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import {
  Configuration,
  EreignisControllerApi,
} from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useLogging } from "@/composables/common/logging.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useEreignisMapper } from "@/composables/vorfaelleundvorkommnisse/ereignisMapper.ts";
import { VORFAELLEUNDVORKOMMNISSE_SERVICE_API_URL } from "@/constants";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { logDebug } = useLogging("ereignisService");

const userNotificationService = useUserNotificationService();
const { toModel, toDto } = useEreignisMapper();

export function useEreignisService() {
  const ereignisControllerApi = new EreignisControllerApi(
    new Configuration({
      basePath: VORFAELLEUNDVORKOMMNISSE_SERVICE_API_URL,
    })
  );

  function getEreignisse(wahlbezirkID: string): Promise<WahlbezirkEreignisse> {
    return ereignisControllerApi
      .getEreignisse(wahlbezirkID)
      .then((response) => toModel(response.data));
  }

  async function saveEreignisse(
    wahlbezirkID: string,
    ereignisse: WahlbezirkEreignisse,
    sendNotification = true
  ): Promise<void> {
    const ereignisseWriteDto = toDto(ereignisse);

    try {
      await ereignisControllerApi.postEreignisse(
        wahlbezirkID,
        ereignisseWriteDto
      );
      if (sendNotification) {
        userNotificationService.addNotification(
          "Die Ereignisse wurden erfolgreich gespeichert",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Das Speichern der Ereignisse schlug fehl.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      logDebug("Das Speichern der Ereignisse schlug fehl.", error);
    }
  }

  return {
    getEreignisse,
    saveEreignisse,
  };
}
