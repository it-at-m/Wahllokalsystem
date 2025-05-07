import type { Wahl } from "@/types/wahl/Wahl.ts";

import { WahlenControllerApi } from "@/api/wls-clients/generated-basisdaten-api";
import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useWahlMapper } from "@/composables/wahl/wahlMapper.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";

const { toModel } = useWahlMapper();

export function useWahlService() {
  const wahlenControllerApi = new WahlenControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  function loadWahlen(wahltagID: string): Promise<Wahl[]> {
    try {
      return wahlenControllerApi
        .getWahlen(wahltagID)
        .then((response) => response.data.map(toModel));
    } catch (error) {
      throw error;
    }
  }

  return {
    loadWahlen,
  };
}
