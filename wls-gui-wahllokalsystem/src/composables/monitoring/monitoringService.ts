import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import {
  Configuration,
  WaehleranzahlControllerApi,
} from "@/api/wls-clients/generated-monitoring-api";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useWahlbeteiligungMapper } from "@/composables/monitoring/wahlbeteiligungMapper.ts";
import { MONITORING_SERVICE_API_URL } from "@/constants.ts";

const { toDto, toModel } = useWahlbeteiligungMapper();
const { applyLocalTimezoneOffset } = useDateTimeFormatter();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

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
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : undefined;
    } catch (e) {
      console.debug(e);
    }
  }

  async function postWahlbeteiligung(
    wahlbezirkID: string,
    wahlID: string,
    waehleranzahl: number
  ): Promise<void> {
    const wahlbeteiligung: Waehleranzahl = {
      anzahlWaehler: waehleranzahl,
      uhrzeit: applyLocalTimezoneOffset(new Date()),
    };

    try {
      await waehlerAnzahlControllerApi.postWahlbeteiligung(
        wahlbezirkID,
        wahlID,
        toDto(wahlbeteiligung)
      );
    } catch (e) {
      console.debug(e);
      throw new Error("postWahlbeteiligung failed");
    }
  }

  return { getWahlbeteiligung, postWahlbeteiligung };
}
