import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

import {
  Configuration,
  StimmzettelumschlaegeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toDto, toModel } = useErgebnisermittlungMapper();
const { addNotification } = useUserNotificationService();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useErgebnisermittlungService() {
  const stimmzettelumschlaegeControllerAPI =
    new StimmzettelumschlaegeControllerApi(
      new Configuration({
        basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
      })
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
  };
}
