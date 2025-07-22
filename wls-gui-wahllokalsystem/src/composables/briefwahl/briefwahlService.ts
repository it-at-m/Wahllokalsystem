import type { BeanstandeteWahlbriefeCreateDTO } from "@/api/wls-clients/generated-briefwahl-api";

import {
  BeanstandeteWahlbriefeControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-briefwahl-api";
import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BRIEFWAHL_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel } = useBeanstandeteWahlbriefeMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { addNotification } = useUserNotificationService();

export function useBriefwahlService() {
  const beanstandeteWahlbriefeControllerAPI =
    new BeanstandeteWahlbriefeControllerApi(
      new Configuration({ basePath: BRIEFWAHL_SERVICE_API_URL })
    );

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
    } catch {
      addNotification(
        "Die beanstandeten Wahlbriefe konnten nicht gespeichert werden.",
        UserNotificationCategoryEnum.ERROR
      );
    }

    return;
  }

  return { getBeanstandeteWahlbriefe, postBeanstandeteWahlbriefe };
}
