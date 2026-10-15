import {
  Configuration,
  WahllokalZustandControllerApi,
} from "@/api/wls-clients/generated-monitoring-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { MONITORING_SERVICE_API_URL } from "@/constants.ts";

const { axiosConfigWrapper } = useCommonApiUtils();

export function useMonitoringService() {
  const wahllokalZustandControllerApi = new WahllokalZustandControllerApi(
    new Configuration({ basePath: MONITORING_SERVICE_API_URL })
  );

  async function postLastSeen(wahlbezirkID: string, teamID: string) {
    try {
      await wahllokalZustandControllerApi.postLastSeen(
        wahlbezirkID,
        teamID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
    } catch {
      throw new Error("postLastSeen failed");
    }
  }

  return { postLastSeen };
}
