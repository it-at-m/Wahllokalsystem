import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";

import {
  Configuration,
  WahltageControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";

export default function useWahltagService() {
  const adminWahltageAPI = new WahltageControllerApi(
    new Configuration({
      basePath: ADMIN_SERVICE_API_URL,
    })
  );
  const { mapWahltagDtoToWahltagEvent } = useWahltagMapper();

  async function getWahltage(): Promise<Wahltag[]> {
    const wahltage = await adminWahltageAPI
      .getWahltage()
      .then((response) => response.data);

    const wahltageGroupByDatum = groupWahltagDtosByWahltag(wahltage);

    const result: Wahltag[] = [];
    wahltageGroupByDatum.forEach((wahltage, wahltagDatum) => {
      result.push({
        wahltag: wahltagDatum,
        events: wahltage.map((dto) => mapWahltagDtoToWahltagEvent(dto)),
      });
    });
    return result;
  }

  function groupWahltagDtosByWahltag(
    dtos: WahltagDTO[]
  ): Map<string, WahltagDTO[]> {
    const groupedWahltage = new Map<string, WahltagDTO[]>();

    dtos.reduce((group, wahltagDTO) => {
      const { wahltag } = wahltagDTO;

      const currentGroupValue = group.get(wahltag) ?? [];
      currentGroupValue.push(wahltagDTO);
      group.set(wahltag, currentGroupValue);

      return group;
    }, groupedWahltage);

    return groupedWahltage;
  }

  return {
    getWahltage,
  };
}
