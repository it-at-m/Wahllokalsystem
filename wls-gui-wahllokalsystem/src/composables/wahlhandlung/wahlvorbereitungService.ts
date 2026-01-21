import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlhandlung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlhandlung/Urnenwahlvorbereitung.ts";
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

import {
  BriefwahlvorbereitungControllerApi,
  EroeffnungsUhrzeitControllerApi,
  UrnenwahlSchliessungsUhrzeitControllerApi,
  UrnenwahlvorbereitungControllerApi,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import { Configuration } from "@/api/wls-clients/generated-wahlvorstand-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorbereitungMapper } from "@/composables/wahlhandlung/wahlvorbereitungMapper.ts";
import { WAHLVORBEREITUNG_SERVICE_API_URL } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();
const {
  toUrnenwahlSchliessungsuhrzeitModel,
  toUrnenwahlSchliessungsuhrzeitDTO,
  toEroeffnungsuhrzeitWriteDTO,
  toUrnenwahlvorbereitungModel,
  toUrnenwahlvorbereitungWriteDto,
  toBriefwahlvorbereitungWriteDto,
  toBriefwahlvorbereitungModel,
} = useWahlvorbereitungMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { isValidDate } = useDateTimeUtils();

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
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<UrnenwahlSchliessungsuhrzeit | null> {
    try {
      const response =
        await urnenwahlSchliessungsUhrzeitControllerAPI.getUrnenwahlSchliessungsUhrzeit(
          wahlbezirkID
        );
      const responseData = getNullOn204OrElseResponseData(response);

      if (!responseData) {
        return null;
      }

      return toUrnenwahlSchliessungsuhrzeitModel(responseData);
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim Laden der Schliessungsuhrzeit.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function getEroeffnungsuhrzeit(
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<Date | null> {
    try {
      const response =
        await eroeffnungsuhrzeitControllerAPI.getEroeffnungsuhrzeit(
          wahlbezirkID
        );
      const responseData = getNullOn204OrElseResponseData(response);

      if (!responseData) {
        return null;
      }

      const eroeffnungsuhrzeit = new Date(responseData.eroeffnungsuhrzeit);
      return isValidDate(eroeffnungsuhrzeit) ? eroeffnungsuhrzeit : null;
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Eröffnungsuhrzeit konnte nicht geladen werden",
          UserNotificationCategoryEnum.ERROR
        );
      }
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
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<Urnenwahlvorbereitung | null> {
    try {
      const response =
        await urnenwahlvorbereitungControllerAPI.getUrnenwahlVorbereitung(
          wahlbezirkID
        );
      const responseData = getNullOn204OrElseResponseData(response);
      if (responseData) {
        useStatusStore().isWahlumgebungErfasst = true;
        return toUrnenwahlvorbereitungModel(responseData);
      } else {
        return null;
      }
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim Laden der Urnenwahlvorbereitung.",
          UserNotificationCategoryEnum.ERROR
        );
      }
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
      useStatusStore().isWahlumgebungErfasst = true;
    } catch (error) {
      userNotificationService.addNotification(
        "Speichern der Urnenwahlvorbereitung fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function getBriefwahlvorbereitung(
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<Wahlvorbereitung> {
    try {
      return await briefwahlvorbereitungControllerAPI
        .getBriefwahlvorbereitung(wahlbezirkID)
        .then((response) => toBriefwahlvorbereitungModel(response.data));
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Fehler beim Laden der Briefwahlvorbereitung.",
          UserNotificationCategoryEnum.ERROR
        );
      }
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
    getEroeffnungsuhrzeit,
    postEroeffnungsuhrzeit,
    postUrnenwahlSchliessungsuhrzeit,
    getUrnenwahlvorbereitung,
    postUrnenwahlvorbereitung,
    postBriefwahlvorbereitung,
    getBriefwahlvorbereitung,
  };
}
