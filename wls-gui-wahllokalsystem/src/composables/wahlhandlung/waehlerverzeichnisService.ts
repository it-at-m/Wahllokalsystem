import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";

import {
  Configuration,
  WaehlerverzeichnisControllerApi,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWaehlerverzeichnisMapper } from "@/composables/wahlhandlung/waehlerverzeichnisMapper.ts";
import { WAHLVORBEREITUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useWaehlerverzeichnisService() {
  const waehlerverzeichnisControllerAPI = new WaehlerverzeichnisControllerApi(
    new Configuration({
      basePath: WAHLVORBEREITUNG_SERVICE_API_URL,
    })
  );
  const { toPflegeWaehlerverzeichnis, toWaehlerverzeichnisWriteDTO } =
    useWaehlerverzeichnisMapper();
  const { addNotification } = useUserNotificationService();
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();

  async function postWaehlerverzeichnis(
    wahlbezirkID: string,
    waehlerverzeichnisNummer: number,
    pflegeWaehlerverzeichnis: PflegeWaehlerverzeichnis,
    sendNotification = true
  ): Promise<void> {
    try {
      await waehlerverzeichnisControllerAPI.postWaehlerverzeichnis(
        wahlbezirkID,
        waehlerverzeichnisNummer,
        toWaehlerverzeichnisWriteDTO(pflegeWaehlerverzeichnis)
      );
      if (sendNotification) {
        addNotification(
          "Die Hinweise zu Wahlscheinen wurden erfolgreich gespeichert.",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Das Speichern der Hinweise zu Wahlscheinen schlug fehl.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function getWaehlerverzeichnis(
    wahlbezirkID: string,
    waehlerverzeichnisNummer: number,
    sendNotification = true
  ): Promise<PflegeWaehlerverzeichnis> {
    try {
      const response =
        await waehlerverzeichnisControllerAPI.getWaehlerverzeichnis(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
      const responseData = getNullOn204OrElseResponseData(response);
      if (responseData) {
        return toPflegeWaehlerverzeichnis(responseData);
      } else {
        return createDefaultPflegeWaehlerverzeichnis();
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Das Laden der Hinweise zu Wahlscheinen schlug fehl.",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  function createDefaultPflegeWaehlerverzeichnis(): PflegeWaehlerverzeichnis {
    return {
      nachtraeglicheBerichtigung: false,
      waehlerverzeichnisUnchanged: true,
      mitteilungUeberUngueltigeWahlscheineErhalten: true,
    };
  }

  return {
    createDefaultPflegeWaehlerverzeichnis,
    getWaehlerverzeichnis,
    postWaehlerverzeichnis,
  };
}
