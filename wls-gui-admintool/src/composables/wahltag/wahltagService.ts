import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import {
  Configuration,
  WahltageControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahltagDtoUtils } from "@/composables/wahltag/WahltagDtoUtils.ts";
import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";
import { compareByNummerAsc } from "@/types/wahltag/WahltagEvent.ts";

export default function useWahltagService() {
  const adminWahltageAPI = new WahltageControllerApi(
    new Configuration({
      basePath: ADMIN_SERVICE_API_URL,
    })
  );
  const { mapGroupedWahltagDtosToWahltage } = useWahltagMapper();
  const { addNotification } = useUserNotificationService();
  const { groupWahltagDtosByWahltag } = useWahltagDtoUtils();

  async function getWahltage(isLoading?: Ref<boolean>): Promise<Wahltag[]> {
    updateLoading(true, isLoading);

    const result: Wahltag[] = [];
    try {
      const wahltagDtos = await adminWahltageAPI
        .getWahltage()
        .then((response) => response.data);

      const wahltageGroupByDatum = groupWahltagDtosByWahltag(wahltagDtos);
      result.push(...mapGroupedWahltagDtosToWahltage(wahltageGroupByDatum));
      result.forEach((wahltag) => wahltag.events.sort(compareByNummerAsc));
    } catch {
      addNotification("Wahltage konnten nicht geladen werden", "Error");
    }

    updateLoading(false, isLoading);

    return result;
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
