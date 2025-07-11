import { storeToRefs } from "pinia";

import {
  BeanstandeteWahlbriefeControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-briefwahl-api";
import { useBeanstandeteWahlbriefeMapper } from "@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { BRIEFWAHL_SERVICE_API_URL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { toModel } = useBeanstandeteWahlbriefeMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useBriefwahlService() {
  const beanstandeteWahlbriefeControllerAPI =
    new BeanstandeteWahlbriefeControllerApi(
      new Configuration({ basePath: BRIEFWAHL_SERVICE_API_URL })
    );

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  async function getBeanstandeteWahlbriefe(waehlerverzeichnisNummer: number) {
    try {
      const response =
        await beanstandeteWahlbriefeControllerAPI.getBeanstandeteWahlbriefe(
          currentUserWahlbezirkID.value,
          waehlerverzeichnisNummer
        );
      const responseData = getNullOn204OrElseResponseData(response);

      return responseData ? toModel(responseData) : null;
    } catch (e) {
      console.debug(e);
    }
  }

  return { getBeanstandeteWahlbriefe };
}
