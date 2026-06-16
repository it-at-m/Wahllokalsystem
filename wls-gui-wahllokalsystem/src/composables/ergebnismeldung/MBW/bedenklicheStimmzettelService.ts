import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";

import {
  Configuration,
  MbwBedenklicheStimmzettelControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useBedenklicherStimmzettelMapper } from "@/composables/ergebnismeldung/MBW/bedenklicherStimmzettelMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { addNotification } = useUserNotificationService();

export function useBedenklicheStimmzettelService() {
  const { setStepDone } = useWorkflowStore();

  const ergebnisMeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });
  const bedenklicheStimmzettelControllerApi =
    new MbwBedenklicheStimmzettelControllerApi(ergebnisMeldungConfiguration);
  const { toModel, toDTO } = useBedenklicherStimmzettelMapper();

  async function getBedenklicheStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const response =
        await bedenklicheStimmzettelControllerApi.getBedenklicheStimmzettelByOrderIndexAsc(
          wahlID,
          wahlbezirkID
        );

      if (sendNotification) {
        addNotification(
          "Bedenkliche Stimmzettel erfolgreich geladen",
          UserNotificationCategoryEnum.SUCCESS
        );
      }

      const responseData = getNullOn204OrElseResponseData(response);
      if (responseData) {
        setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_STAPEL_E);
        return responseData.map((dto) => toModel(dto));
      } else {
        return null;
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Laden von bedenklichen Stimmzetteln fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(
        `Laden von bedenklichen Stimmzetteln für wahlID > ${wahlID}, wahlbezirkID > ${wahlbezirkID} fehlgeschlagen`,
        { cause: error }
      );
    }
  }

  async function saveBedenklicheStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    bedenklicheStimmzettel: BedenklicherStimmzettel[],
    sendNotification = true
  ) {
    try {
      const dtoToSend = bedenklicheStimmzettel.map((model) => toDTO(model));
      await bedenklicheStimmzettelControllerApi.setBedenklicheStimmzettel(
        wahlID,
        wahlbezirkID,
        dtoToSend
      );
      setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_STAPEL_E);
      if (sendNotification) {
        addNotification(
          "Speichern der bedenklichen Stimmzettel erfolgreich",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Speichern der bedenklichen Stimmzettel fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(
        `Speichern der bedenklichen Stimmzettel fehlgeschlagen für wahlID > ${wahlID}, wahlbezirkID > ${wahlbezirkID}`,
        {
          cause: error,
        }
      );
    }
  }

  return {
    getBedenklicheStimmzettel,
    saveBedenklicheStimmzettel,
  };
}
