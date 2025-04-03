import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import {
  Configuration,
  WahltageControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";

export default function useWahltagService() {
  const adminWahltageAPI = new WahltageControllerApi(
    new Configuration({
      basePath: ADMIN_SERVICE_API_URL,
    })
  );
  const { mapWahltagDtoToWahltagEvent } = useWahltagMapper();
  const { addNotification } = useUserNotificationService();

  async function getWahltage(isLoading?: Ref<boolean>): Promise<Wahltag[]> {
    updateLoading(true, isLoading);

    const result: Wahltag[] = [];
    try {
      const wahltage = await adminWahltageAPI
        .getWahltage()
        .then((response) => response.data);

      const wahltageGroupByDatum = groupWahltagDtosByWahltag(wahltage);

      wahltageGroupByDatum.forEach((wahltage, wahltagDatum) => {
        result.push({
          wahltag: wahltagDatum,
          events: wahltage.map((dto) => mapWahltagDtoToWahltagEvent(dto)),
        });
      });
    } catch (error) {
      addNotification("Wahltage konnten nicht geladen werden", "Error");
    }

    updateLoading(false, isLoading);

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

  function updateLoading(loadingState: boolean, loadingRef?: Ref<boolean>) {
    if (loadingRef) {
      loadingRef.value = loadingState;
    }
  }

  return {
    getWahltage,
  };
}
