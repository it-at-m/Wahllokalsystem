import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

import {
  BegruendungControllerApi,
  Configuration,
  StimmzettelumschlaegeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/ergebnisMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toDto, toModel } = useErgebnisermittlungMapper();
const { toBegruendungModel } = useErgebnisMapper();
const { addNotification } = useUserNotificationService();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useErgebnisermittlungService() {
  const ergebnisMeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const stimmzettelumschlaegeControllerAPI =
    new StimmzettelumschlaegeControllerApi(ergebnisMeldungConfiguration);

  const begruendungControllerApi = new BegruendungControllerApi(
    ergebnisMeldungConfiguration
  );

  async function postStimmzettelumschlaege(
    wahlID: string,
    wahlbezirkID: string,
    stimmzettelumschlaege: Stimmzettelumschlaege,
    wahlbezirksArt: WahlbezirksArtEnum,
    wahlName: string,
    sendNotification = true
  ): Promise<void> {
    try {
      await stimmzettelumschlaegeControllerAPI.postStimmzettelumschlaege(
        wahlID,
        wahlbezirkID,
        toDto(stimmzettelumschlaege, wahlID, wahlbezirkID)
      );
      if (sendNotification) {
        addNotification(
          `${_getStimmzettelTermBasedOnWahlbezirkArt(wahlbezirksArt)} für ${wahlName} erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          `Speichern der ${_getStimmzettelTermBasedOnWahlbezirkArt(wahlbezirksArt)} für ${wahlName} fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function getStimmzettelumschlaege(
    wahlID: string,
    wahlbezirkID: string,
    wahlbezirksArt: WahlbezirksArtEnum,
    wahlName: string,
    sendNotification = true
  ) {
    try {
      const response =
        await stimmzettelumschlaegeControllerAPI.getStimmzettelumschlaege(
          wahlID,
          wahlbezirkID
        );
      if (sendNotification) {
        addNotification(
          `${_getStimmzettelTermBasedOnWahlbezirkArt(wahlbezirksArt)} für ${wahlName} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : null;
    } catch (e) {
      if (sendNotification) {
        addNotification(
          `Laden der ${_getStimmzettelTermBasedOnWahlbezirkArt(wahlbezirksArt)} für ${wahlName} fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw e;
    }
  }

  async function getBegruendungStimmzettelumschlaege(
    wahlID: string,
    wahlbezirkID: string,
    wahlbezirksArt: WahlbezirksArtEnum,
    wahlName: string,
    sendNotification = true
  ) {
    try {
      const response = await begruendungControllerApi.getBegruendung(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.StimmzettelUmschlaege
      );
      if (sendNotification) {
        addNotification(
          `Begründung ${_getStimmzettelTermBasedOnWahlbezirkArt(wahlbezirksArt)} für ${wahlName} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toBegruendungModel(responseData) : null;
    } catch (e) {
      if (sendNotification) {
        addNotification(
          `Laden der Begründung ${_getStimmzettelTermBasedOnWahlbezirkArt(wahlbezirksArt)} für ${wahlName} fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw e;
    }
  }

  function _getStimmzettelTermBasedOnWahlbezirkArt(
    wahlbezirksArt: WahlbezirksArtEnum
  ) {
    switch (wahlbezirksArt) {
      case "UWB":
        return "Stimmzettel";
      case "BWB":
        return "Stimmzettelumschläge";
    }
  }

  return {
    postStimmzettelumschlaege,
    getStimmzettelumschlaege,
    getBegruendungStimmzettelumschlaege,
  };
}
