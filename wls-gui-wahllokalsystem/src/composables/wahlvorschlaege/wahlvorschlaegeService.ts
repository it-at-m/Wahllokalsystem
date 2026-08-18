import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import {
  Configuration,
  WahlvorschlaegeControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useWahlvorschlaegeMapper } from "@/composables/wahlvorschlaege/wahlvorschlaegeMapper.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";

const { toModel } = useWahlvorschlaegeMapper();
const {
  sortWahlvorschlaegeByOrdnungszahl,
  sortKandidatenByListenPositionInplace,
} = useWahlvorschlagUtils();

export function useWahlvorschlaegeService() {
  const wahlvorschlaegeControllerAPI = new WahlvorschlaegeControllerApi(
    new Configuration({ basePath: BASISDATEN_SERVICE_API_URL })
  );

  async function getWahlvorschlaege(wahlID: string, wahlbezirkID: string) {
    try {
      const response = await wahlvorschlaegeControllerAPI.getWahlvorschlaege(
        wahlID,
        wahlbezirkID
      );
      const wahlvorschlaege = toModel(response.data);
      await _sortWahlvorschlaege(wahlvorschlaege);
      return wahlvorschlaege;
    } catch {
      throw new Error("GetWahlvorschlaege failed");
    }
  }

  async function _sortWahlvorschlaege(wahlvorschlaege: Wahlvorschlaege) {
    sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege);
    wahlvorschlaege.wahlvorschlaege.forEach((wahlvorschlag) => {
      sortKandidatenByListenPositionInplace(wahlvorschlag);
    });
  }

  return { getWahlvorschlaege };
}
