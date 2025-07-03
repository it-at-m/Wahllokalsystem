import { storeToRefs } from "pinia";

import {
  BeanstandeteWahlbriefeControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-briefwahl-api";
import { BRIEFWAHL_SERVICE_API_URL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useBriefwahlService() {
  const beanstandeteWahlbriefeControllerAPI =
    new BeanstandeteWahlbriefeControllerApi(
      new Configuration({ basePath: BRIEFWAHL_SERVICE_API_URL })
    );

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  async function getBeanstandeteWahlbriefe() {
    try {
      const response =
        await beanstandeteWahlbriefeControllerAPI.getBeanstandeteWahlbriefe(
          currentUserWahlbezirkID.value,
          1 // todo: woher kommmt die wählerverzeichnisnummer?
        );
      return response.data;
    } catch (e) {
      console.debug(e);
    }
  }

  return { getBeanstandeteWahlbriefe };
}
