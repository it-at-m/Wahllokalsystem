import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

import {
  EroeffnungsUhrzeitControllerApi,
  UrnenwahlSchliessungsUhrzeitControllerApi,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import { Configuration } from "@/api/wls-clients/generated-wahlvorstand-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorbereitungMapper } from "@/composables/wahlvorbereitung/wahlvorbereitungMapper.ts";
import { WAHLVORBEREITUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();
const {
  toUrnenwahlSchliessungsuhrzeitModel,
  toUrnenwahlSchliessungsuhrzeitDTO,
  toEroeffnungsuhrzeitWriteDTO,
} = useWahlvorbereitungMapper();

export function useWahlvorbereitungService() {
  const wahlvorbereitungsServiceConfigurations = new Configuration({
    basePath: WAHLVORBEREITUNG_SERVICE_API_URL,
  });
  const urnenwahlSchliessungsUhrzeitControllerAPI =
    new UrnenwahlSchliessungsUhrzeitControllerApi(
      wahlvorbereitungsServiceConfigurations
    );
  const eroeffnungsuhrzeitControllerAPI = new EroeffnungsUhrzeitControllerApi(
    wahlvorbereitungsServiceConfigurations
  );

  async function getUrnenwahlSchliessungsUhrzeit(
    wahlbezirkID: string
  ): Promise<UrnenwahlSchliessungsuhrzeit> {
    try {
      return await urnenwahlSchliessungsUhrzeitControllerAPI
        .getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)
        .then((response) => toUrnenwahlSchliessungsuhrzeitModel(response.data));
    } catch (error) {
      userNotificationService.addNotification(
        "Fehler beim Laden der Schliessungsuhrzeit.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postEroeffnungsuhrzeit(
    wahlbezirkID: string,
    eroeffnungsuhrzeit: Date
  ): Promise<void> {
    try {
      await eroeffnungsuhrzeitControllerAPI.postEroeffnungsuhrzeit(
        wahlbezirkID,
        toEroeffnungsuhrzeitWriteDTO(eroeffnungsuhrzeit)
      );
      userNotificationService.addNotification(
        "Eröffnungsuhrzeit erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Eröffnungsuhrzeit fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postUrnenwahlSchliessungsuhrzeit(
    wahlbezirkID: string,
    schliessungsUhrzeit: Date
  ): Promise<void> {
    const schliessungsuhrzeitWriteDTO =
      toUrnenwahlSchliessungsuhrzeitDTO(schliessungsUhrzeit);

    try {
      await urnenwahlSchliessungsUhrzeitControllerAPI.postUrnenwahlSchliessungsUhrzeit(
        wahlbezirkID,
        schliessungsuhrzeitWriteDTO
      );
      userNotificationService.addNotification(
        "Schliessungsuhrzeit erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Schliessungsuhrzeit fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    getUrnenwahlSchliessungsUhrzeit,
    postEroeffnungsuhrzeit,
    postUrnenwahlSchliessungsuhrzeit,
  };
}
