import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { UrnenwahlSchliessungsUhrzeitControllerApi } from "@/api/wls-clients/generated-wahlvorbereitung-api";
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
    return await urnenwahlSchliessungsUhrzeitControllerAPI
      .getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)
      .then((response) => toModel(response.data));
  }

  async function postUrnenwahlSchliessungsuhrzeit(
    wahlbezirkID: string,
    schliessungsUhrzeit: UrnenwahlSchliessungsuhrzeit
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
