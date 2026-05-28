import type { BeanstandeteWahlbriefeCreateDTO } from "@/api/wls-clients/generated-briefwahl-api";
import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import {
  BeanstandeteWahlbriefeControllerApi,
  Configuration,
  WahlbriefdatenControllerApi,
} from "@/api/wls-clients/generated-briefwahl-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useBriefwahlMapper } from "@/composables/briefwahl/briefwahlMapper.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BRIEFWAHL_SERVICE_API_URL } from "@/constants.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useBeanstandeteWahlbriefeMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { addNotification } = useUserNotificationService();

const { toWahlbriefdatenModel, toWahlbriefdatenWriteDTO } =
  useBriefwahlMapper();
const { logDebug } = useLogging("briefwahlService");

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
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const response =
        await beanstandeteWahlbriefeControllerAPI.getBeanstandeteWahlbriefe(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
      if (sendNotification) {
        addNotification(
          "Die beanstandeten Wahlbriefe wurden erfolgreich geladen.",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);

      if (responseData) {
        useWorkflowStore().isWahlbriefeZulassenErfasst = true;
        return toModel(responseData);
      } else {
        return null;
      }
    } catch (e) {
      const errorMessage =
        "Die beanstandeten Wahlbriefe konnten nicht geladen werden.";
      if (sendNotification) {
        addNotification(errorMessage, UserNotificationCategoryEnum.ERROR);
      }
      logDebug(errorMessage, e);
      throw new Error("Get beanstandete Wahlbriefe Failed", { cause: e });
    }
  }

  async function postBeanstandeteWahlbriefe(
    wahlenGroupedByWvzNr: Map<number, Wahl[]>,
    wahlbezirkID: string
  ) {
    try {
      for (const [wvzNr, wahlenWithWvzNr] of wahlenGroupedByWvzNr.entries()) {
        const beanstandeteWahlbriefeDTO: BeanstandeteWahlbriefeCreateDTO = {
          beanstandeteWahlbriefe: {},
        };

        wahlenWithWvzNr.map((wahl) => {
          if (
            wahl.beanstandeteWahlbriefe &&
            wahl.beanstandeteWahlbriefe.every((grund) => grund !== null)
          ) {
            beanstandeteWahlbriefeDTO.beanstandeteWahlbriefe[wahl.wahlID] =
              wahl.beanstandeteWahlbriefe.map(
                (grund) => grund?.toString() ?? ""
              );
          }
        });

        await beanstandeteWahlbriefeControllerAPI.setBeanstandeteWahlbriefe(
          wahlbezirkID,
          wvzNr,
          beanstandeteWahlbriefeDTO
        );
      }
      useWorkflowStore().isWahlbriefeZulassenErfasst = true;
      addNotification(
        "Die beanstandeten Wahlbriefe wurden erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (e) {
      const errorMessage =
        "Die beanstandeten Wahlbriefe konnten nicht gespeichert werden.";
      logDebug(errorMessage, e);
      addNotification(errorMessage, UserNotificationCategoryEnum.ERROR);
      throw new Error("Post beanstandete Wahlbriefe Failed", { cause: e });
    }
  }

  async function getWahlbriefdaten(
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<Wahlbriefdaten> {
    try {
      const response =
        await wahlbriefdatenControllerApi.getWahlbriefdaten(wahlbezirkID);
      const responseData = getNullOn204OrElseResponseData(response);
      if (responseData) {
        useWorkflowStore().isWahlbriefeErfassenErfasst = true;
        return toWahlbriefdatenModel(responseData);
      } else {
        return {
          wahlbriefe: undefined,
          verzeichnisseUngueltige: undefined,
          nachtraege: undefined,
          nachtraeglichUeberbrachte: undefined,
          zeitNachtraeglichUeberbrachte: undefined,
        };
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Fehler beim Laden der Wahlbriefdaten.",
          UserNotificationCategoryEnum.ERROR
        );
      }
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
      useWorkflowStore().isWahlbriefeErfassenErfasst = true;
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
