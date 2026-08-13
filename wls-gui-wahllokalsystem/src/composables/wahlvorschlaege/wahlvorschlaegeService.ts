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
      return toModel(response.data);
    } catch {
      throw new Error("GetWahlvorschlaege failed");
    }
  }

  async function loadAndSortWahlvorschlaege(
    wahlID: string,
    wahlbezirkID: string
  ) {
    return getWahlvorschlaege(wahlID, wahlbezirkID)
      .then((wahlvorschlaege) =>
        sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege)
      )
      .then((wahlvorschlaege) => {
        wahlvorschlaege.wahlvorschlaege.forEach((wahlvorschlag) => {
          sortKandidatenByListenPositionInplace(wahlvorschlag);
        });
        return wahlvorschlaege;
      });
  }

  return { getWahlvorschlaege, loadAndSortWahlvorschlaege };
}
