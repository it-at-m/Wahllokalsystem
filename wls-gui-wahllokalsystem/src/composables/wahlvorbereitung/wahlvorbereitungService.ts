import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";
import type { Wahlvorbereitung } from "@/types/wahlvorbereitung/Wahlvorbereitung.ts";

import {
  BriefwahlvorbereitungControllerApi,
  EroeffnungsUhrzeitControllerApi,
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
  toUrnenwahlSchliessungsuhrzeitModel,
  toUrnenwahlSchliessungsuhrzeitDTO,
  toEroeffnungsuhrzeitWriteDTO,
  toUrnenwahlvorbereitungModel,
  toUrnenwahlvorbereitungWriteDto,
  toBriefwahlvorbereitungModel,
  toBriefwahlvorbereitungWriteDto,
} = useWahlvorbereitungMapper();

export function useWahlvorbereitungService() {
  const wahlvorbereitungsServiceConfiguration = new Configuration({
    basePath: WAHLVORBEREITUNG_SERVICE_API_URL,
  });
  const urnenwahlSchliessungsUhrzeitControllerAPI =
    new UrnenwahlSchliessungsUhrzeitControllerApi(
      wahlvorbereitungsServiceConfiguration
    );
  const eroeffnungsuhrzeitControllerAPI = new EroeffnungsUhrzeitControllerApi(
    wahlvorbereitungsServiceConfiguration
  );
  const urnenwahlvorbereitungControllerAPI =
    new UrnenwahlvorbereitungControllerApi(
      wahlvorbereitungsServiceConfiguration
    );
  const briefwahlvorbereitungControllerAPI =
    new BriefwahlvorbereitungControllerApi(
      wahlvorbereitungsServiceConfiguration
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

  async function getUrnenwahlvorbereitung(
    wahlbezirkID: string
  ): Promise<Urnenwahlvorbereitung> {
    try {
      return await urnenwahlvorbereitungControllerAPI
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
      await urnenwahlvorbereitungControllerAPI.postUrnenwahlvorbereitung(
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

  async function getBriefwahlvorbereitung(
    wahlbezirkID: string
  ): Promise<Wahlvorbereitung> {
    try {
      return await briefwahlvorbereitungControllerAPI
        .getBriefwahlvorbereitung(wahlbezirkID)
        .then((response) => toBriefwahlvorbereitungModel(response.data));
    } catch (error) {
      userNotificationService.addNotification(
        "Fehler beim Laden der Briefwahlvorbereitung.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postBriefwahlvorbereitung(
    wahlbezirkID: string,
    briefwahlvorbereitung: Wahlvorbereitung
  ): Promise<void> {
    const briefwahlvorbereitungWriteDTO = toBriefwahlvorbereitungWriteDto(
      briefwahlvorbereitung
    );

    try {
      await briefwahlvorbereitungControllerAPI.postBriefwahlvorbereitung(
        wahlbezirkID,
        briefwahlvorbereitungWriteDTO
      );
      userNotificationService.addNotification(
        "Briefwahlvorbereitung erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Briefwahlvorbereitung fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    getUrnenwahlSchliessungsUhrzeit,
    postEroeffnungsuhrzeit,
    postUrnenwahlSchliessungsuhrzeit,
    getUrnenwahlvorbereitung,
    postUrnenwahlvorbereitung,
    getBriefwahlvorbereitung,
    postBriefwahlvorbereitung,
  };
}
