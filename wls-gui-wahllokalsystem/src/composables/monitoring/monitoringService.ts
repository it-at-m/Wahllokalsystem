import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import {
  Configuration,
  WaehleranzahlControllerApi,
  WahllokalZustandControllerApi,
} from "@/api/wls-clients/generated-monitoring-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useWahlbeteiligungMapper } from "@/composables/monitoring/wahlbeteiligungMapper.ts";
import { MONITORING_SERVICE_API_URL } from "@/constants.ts";

const { toDto, toModel } = useWahlbeteiligungMapper();
const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
  useCommonApiUtils();
const { logDebug } = useLogging("monitoringService");

export function useMonitoringService() {
  const waehlerAnzahlControllerApi = new WaehleranzahlControllerApi(
    new Configuration({ basePath: MONITORING_SERVICE_API_URL })
  );
  const wahllokalZustandControllerApi = new WahllokalZustandControllerApi(
    new Configuration({ basePath: MONITORING_SERVICE_API_URL })
  );

  async function getWahlbeteiligung(wahlID: string, wahlbezirkID: string) {
    try {
      const response = await waehlerAnzahlControllerApi.getWahlbeteiligung(
        wahlID,
        wahlbezirkID
      );
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : undefined;
    } catch (e) {
      logDebug("get wahlbeteiligung failed", e);
    }
  }

  async function postWahlbeteiligung(
    wahlbezirkID: string,
    wahlID: string,
    waehleranzahl: number
  ): Promise<void> {
    const wahlbeteiligung: Waehleranzahl = {
      anzahlWaehler: waehleranzahl,
      uhrzeit: new Date(),
    };

    try {
      await waehlerAnzahlControllerApi.postWahlbeteiligung(
        wahlbezirkID,
        wahlID,
        toDto(wahlbeteiligung)
      );
    } catch (e) {
      logDebug("postWahlbeteiligung failed", e);
      throw new Error("postWahlbeteiligung failed", { cause: e });
    }
  }

  async function postLastSeen(wahlbezirkID: string) {
    try {
      await wahllokalZustandControllerApi.postLastSeen(
        wahlbezirkID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
    } catch {
      throw new Error("postLastSeen failed");
    }
  }

  return { getWahlbeteiligung, postLastSeen, postWahlbeteiligung };
}
