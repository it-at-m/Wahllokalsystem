import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

import { UrnenwahlSchliessungsUhrzeitControllerApi } from "@/api/wls-clients/generated-wahlvorbereitung-api";
import { Configuration } from "@/api/wls-clients/generated-wahlvorstand-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorbereitungMapper } from "@/composables/wahlvorbereitung/wahlvorbereitungMapper.ts";
import { WAHLVORBEREITUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();
const { toModel, toDTO } = useWahlvorbereitungMapper();

export function useWahlvorbereitungService() {
  const urnenwahlSchliessungsUhrzeitControllerAPI =
    new UrnenwahlSchliessungsUhrzeitControllerApi(
      new Configuration({
        basePath: WAHLVORBEREITUNG_SERVICE_API_URL,
      })
    );

  async function getUrnenwahlSchliessungsUhrzeit(
    wahlbezirkID: string
  ): Promise<UrnenwahlSchliessungsuhrzeit> {
    try {
      return await urnenwahlSchliessungsUhrzeitControllerAPI
        .getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)
        .then((response) => toModel(response.data));
    } catch (error) {
      userNotificationService.addNotification(
        "Fehler beim Laden der Schliessungsuhrzeit.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postUrnenwahlSchliessungsuhrzeit(
    wahlbezirkID: string,
    schliessungsUhrzeit: Date
  ): Promise<void> {
    const schliessungsuhrzeitWriteDTO = toDTO(schliessungsUhrzeit);

    try {
      await urnenwahlSchliessungsUhrzeitControllerAPI.postUrnenwahlSchliessungsUhrzeit(
        wahlbezirkID,
        schliessungsuhrzeitWriteDTO
      );
      userNotificationService.addNotification(
        "Schliessungsuhrzeit erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch {
      userNotificationService.addNotification(
        "Speichern der Schliessungsuhrzeit fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
    }
  }

  return { getUrnenwahlSchliessungsUhrzeit, postUrnenwahlSchliessungsuhrzeit };
}
