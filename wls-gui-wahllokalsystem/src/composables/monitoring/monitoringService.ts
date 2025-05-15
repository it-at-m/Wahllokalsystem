import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import {
  Configuration,
  WaehleranzahlControllerApi,
} from "@/api/wls-clients/generated-monitoring-api";
import { useWahlbeteiligungMapper } from "@/composables/monitoring/wahlbeteiligungMapper.ts";
import { MONITORING_SERVICE_API_URL } from "@/constants.ts";

const { toDto, toModel } = useWahlbeteiligungMapper();

export function useMonitoringService() {
  const waehlerAnzahlControllerApi = new WaehleranzahlControllerApi(
    new Configuration({ basePath: MONITORING_SERVICE_API_URL })
  );

  async function getWahlbeteiligung(wahlID: string, wahlbezirkID: string) {
    try {
      const response = await waehlerAnzahlControllerApi.getWahlbeteiligung(
        wahlID,
        wahlbezirkID
      );
      return toModel(response.data);
    } catch (e) {
      console.debug(e);
    }
  }

  async function postWahlbeteiligung(
    wahlbezirkID: string,
    wahlID: string,
    waehleranzahl: Waehleranzahl
  ): Promise<void> {
    const waehleranzahlDTO = toDto(waehleranzahl);

    try {
      await waehlerAnzahlControllerApi.postWahlbeteiligung(
        wahlbezirkID,
        wahlID,
        waehleranzahlDTO
      );
    } catch (e) {
      console.debug(e);
    }
  }

  return { getWahlbeteiligung, postWahlbeteiligung };
}
