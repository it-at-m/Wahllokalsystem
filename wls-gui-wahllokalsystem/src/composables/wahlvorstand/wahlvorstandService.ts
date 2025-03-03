import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";

import {
  Configuration,
  WahlvorstandControllerApi,
} from "@/api/wls-clients/generated-wahlvorstand-api";
import { useWahlvorstandMapper } from "@/composables/wahlvorstand/wahlvorstandMapper";
import { WAHLVORSTAND_SERVICE_API_URL } from "@/constants";

const { toModel, toDto } = useWahlvorstandMapper();

export function useWahlvorstandService() {
  const wahlvorstandControllerApi = new WahlvorstandControllerApi(
    new Configuration({
      basePath: WAHLVORSTAND_SERVICE_API_URL,
    })
  );

  function getWahlvorstand(wahlbezirkID: string): Promise<Wahlvorstand> {
    return wahlvorstandControllerApi
      .getWahlvorstand(wahlbezirkID)
      .then((response) => toModel(response.data));
  }

  async function saveWahlvorstand(
    wahlbezirkID: string,
    wahlvorstand: Wahlvorstand
  ): Promise<{
    updateDatetime: Date;
  }> {
    const now = new Date();
    const wahlvorstandDto = toDto(wahlvorstand, now);

    wahlvorstandControllerApi.postWahlvorstand(wahlbezirkID, wahlvorstandDto);

    return Promise.resolve({
      updateDatetime: now,
    });
  }

  return {
    getWahlvorstand,
    saveWahlvorstand,
  };
}
