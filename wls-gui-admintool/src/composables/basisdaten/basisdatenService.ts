import type { BasisdatenInitProgress } from "@/types/basisdaten/BasisdatenInitProgress.ts";

import {
  AsyncProgressControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useBasisdatenMapper } from "@/composables/basisdaten/basisdatenMapper.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";

export function useBasisdatenService() {
  const basisdatenAsyncProgressAPI = new AsyncProgressControllerApi(
    new Configuration({
      basePath: BASISDATEN_SERVICE_API_URL,
    })
  );
  const { mapAsyncProgressDtoToBasisdatenInitProgress } = useBasisdatenMapper();

  async function getAsyncProgress(): Promise<BasisdatenInitProgress> {
    return await basisdatenAsyncProgressAPI
      .getAsyncProgress()
      .then((response) => response.data)
      .then((asyncProgress) =>
        mapAsyncProgressDtoToBasisdatenInitProgress(asyncProgress)
      );
  }

  return {
    getAsyncProgress,
  };
}
