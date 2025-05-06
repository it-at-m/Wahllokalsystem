import type { Wahl } from "@/types/wahl/Wahl.ts";

import { WahlenControllerApi } from "@/api/wls-clients/generated-basisdaten-api";
import { Configuration } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useWahlenMapper } from "@/composables/wahlen/wahlenMapper.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";

const { toModel } = useWahlenMapper();

export function useWahlService() {
  const wahlenControllerApi = new WahlenControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );

  function loadWahlen(wahltagID: string): Promise<Wahl[]> {
    return wahlenControllerApi
      .getWahlen(wahltagID)
      .then((response) => response.data.map(toModel));
  }

  return {
    loadWahlen,
  };
}
