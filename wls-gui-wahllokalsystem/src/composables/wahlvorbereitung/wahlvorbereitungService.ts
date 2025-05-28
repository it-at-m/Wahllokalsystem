import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

import {
  UrnenwahlSchliessungsUhrzeitControllerApi,
  UrnenwahlvorbereitungControllerApi,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import { Configuration } from "@/api/wls-clients/generated-wahlvorstand-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorbereitungMapper } from "@/composables/wahlvorbereitung/wahlvorbereitungMapper.ts";
import { WAHLVORBEREITUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();
const {
  toModel,
  toDTO,
  toUrnenwahlvorbereitungModel,
  toUrnenwahlvorbereitungWriteDto,
} = useWahlvorbereitungMapper();

export function useWahlvorbereitungService() {
  const urnenwahlSchliessungsUhrzeitControllerAPI =
    new UrnenwahlSchliessungsUhrzeitControllerApi(
      new Configuration({
        basePath: WAHLVORBEREITUNG_SERVICE_API_URL,
      })
    );
  const urnenwahlvorbereitungControllerApi =
    new UrnenwahlvorbereitungControllerApi(
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
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Schliessungsuhrzeit fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function getUrnenwahlvorbereitung(
    wahlbezirkID: string
  ): Promise<Urnenwahlvorbereitung> {
    try {
      return await urnenwahlvorbereitungControllerApi
        .getUrnenwahlVorbereitung(wahlbezirkID)
        .then((response) => toUrnenwahlvorbereitungModel(response.data));
    } catch (error) {
      userNotificationService.addNotification(
        "Fehler beim Laden der Urnenwahlvorbereitung.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postUrnenwahlvorbereitung(
    wahlbezirkID: string,
    urnenwahlvorbereitung: Urnenwahlvorbereitung
  ): Promise<void> {
    const urnenwahlvorbereitungWriteDTO = toUrnenwahlvorbereitungWriteDto(
      urnenwahlvorbereitung
    );

    try {
      await urnenwahlvorbereitungControllerApi.postUrnenwahlvorbereitung(
        wahlbezirkID,
        urnenwahlvorbereitungWriteDTO
      );
      userNotificationService.addNotification(
        "Urnenwahlvorbereitung erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Urnenwahlvorbereitung fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    getUrnenwahlSchliessungsUhrzeit,
    postUrnenwahlSchliessungsuhrzeit,
    getUrnenwahlvorbereitung,
    postUrnenwahlvorbereitung,
  };
}
