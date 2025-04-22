import type { AWerteInitProgress } from "@/types/aWerte/AWerteInitProgress.ts";

import {
  AsyncProgressControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useAWerteMapper } from "@/composables/aWerte/aWerteMapper.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";

export function useAWerteService() {
  const awerteAsyncProgressAPI = new AsyncProgressControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );
  const { asyncProgressDtoToAWerteInitProgress } = useAWerteMapper();

  async function getAWerteProgress(): Promise<AWerteInitProgress> {
    return await awerteAsyncProgressAPI
      .getAsyncProgress()
      .then((response) => response.data)
      .then((asyncprogress) =>
        asyncProgressDtoToAWerteInitProgress(asyncprogress)
      );
  }

  return {
    getAWerteProgress,
  };
}
