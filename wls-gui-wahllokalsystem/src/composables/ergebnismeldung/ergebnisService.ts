import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

import {
  Configuration,
  ErgebnisseControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/ergebnisMapper.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";

const { toModel } = useErgebnisMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useErgebnisService() {
  const ergebnisseControllerAPI = new ErgebnisseControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function getErgebnisse(
    wahlID: string,
    wahlbezirkID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      const response = await ergebnisseControllerAPI.getErgebnisse(
        wahlID,
        wahlbezirkID,
        stapelArt
      );

      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : null;
    } catch {
      throw new Error(`Get Ergebnisse for Stapelart ${stapelArt} failed.`);
    }
  }

  return { getErgebnisse };
}
