import {
  Configuration,
  WahlvorschlaegeControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useWahlvorschlaegeMapper } from "@/composables/wahlvorschlaege/wahlvorschlaegeMapper.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";

const { toModel } = useWahlvorschlaegeMapper();

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

  return { getWahlvorschlaege };
}
