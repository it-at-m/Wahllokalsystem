import type { BeanstandeteWahlbriefeCreateDTO } from "@/api/wls-clients/generated-briefwahl-api";
import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten";

import {
  BeanstandeteWahlbriefeControllerApi,
  Configuration,
  WahlbriefdatenControllerApi,
} from "@/api/wls-clients/generated-briefwahl-api";
import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useBriefwahlMapper } from "@/composables/briefwahl/briefwahlMapper.ts";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BRIEFWAHL_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useBeanstandeteWahlbriefeMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { addNotification } = useUserNotificationService();

const { toWahlbriefdatenModel, toWahlbriefdatenWriteDTO } =
  useBriefwahlMapper();

export function useBriefwahlService() {
  const briefwahlServiceConfiguration = new Configuration({
    basePath: BRIEFWAHL_SERVICE_API_URL,
  });
  const wahlbriefdatenControllerApi = new WahlbriefdatenControllerApi(
    briefwahlServiceConfiguration
  );
  const beanstandeteWahlbriefeControllerAPI =
    new BeanstandeteWahlbriefeControllerApi(briefwahlServiceConfiguration);

  async function getBeanstandeteWahlbriefe(
    waehlerverzeichnisNummer: number,
    wahlbezirkID: string
  ) {
    try {
      const response =
        await beanstandeteWahlbriefeControllerAPI.getBeanstandeteWahlbriefe(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
      const responseData = getNullOn204OrElseResponseData(response);

      return responseData ? toModel(responseData) : null;
    } catch (e) {
      console.debug(e);
      addNotification(
        "Die beanstandeten Wahlbriefe konnten nicht geladen werden.",
        UserNotificationCategoryEnum.ERROR
      );
      throw new Error("Get beanstandete Wahlbriefe Failed");
    }
  }

  async function postBeanstandeteWahlbriefe(
    beanstandeteWahlbriefeDTO: BeanstandeteWahlbriefeCreateDTO,
    wahlbezirkID: string,
    waehlerVerzeichnisNummer: number
  ) {
    try {
      await beanstandeteWahlbriefeControllerAPI.setBeanstandeteWahlbriefe(
        wahlbezirkID,
        waehlerVerzeichnisNummer,
        beanstandeteWahlbriefeDTO
      );
      addNotification(
        "Die beanstandeten Wahlbriefe wurden erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (e) {
      console.debug(e);
      addNotification(
        "Die beanstandeten Wahlbriefe konnten nicht gespeichert werden.",
        UserNotificationCategoryEnum.ERROR
      );
      throw new Error("Post beanstandete Wahlbriefe Failed");
    }
  }

  async function getWahlbriefdaten(
    wahlbezirkID: string
  ): Promise<Wahlbriefdaten> {
    try {
      return await wahlbriefdatenControllerApi
        .getWahlbriefdaten(wahlbezirkID)
        .then((response) => toWahlbriefdatenModel(response.data));
    } catch (error) {
      addNotification(
        "Fehler beim Laden der Wahlbriefdaten.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function postWahlbriefdaten(
    wahlbezirkID: string,
    wahlbriefdaten: Wahlbriefdaten
  ): Promise<void> {
    const wahlbriefdatenWriteDTO = toWahlbriefdatenWriteDTO(wahlbriefdaten);

    try {
      await wahlbriefdatenControllerApi.postWahlbriefdaten(
        wahlbezirkID,
        wahlbriefdatenWriteDTO
      );
      addNotification(
        "Wahlbriefdaten erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      addNotification(
        "Speichern der Wahlbriefdaten fehlgeschlagen.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    getBeanstandeteWahlbriefe,
    postBeanstandeteWahlbriefe,
    getWahlbriefdaten,
    postWahlbriefdaten,
  };
}
