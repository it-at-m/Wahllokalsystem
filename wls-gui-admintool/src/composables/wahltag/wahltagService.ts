import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import {
  Configuration,
  KonfigurierteWahltageControllerApi,
  WahltageControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useApiUtils } from "@/composables/common/apiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahltagDtoUtils } from "@/composables/wahltag/wahltagDtoUtils.ts";
import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";
import { compareByNummerAsc } from "@/types/wahltag/WahltagEvent.ts";

export default function useWahltagService() {
  const adminWahltageAPI = new WahltageControllerApi(
    new Configuration({
      basePath: ADMIN_SERVICE_API_URL,
    })
  );
  const adminKonfigurierteWahltageAPI = new KonfigurierteWahltageControllerApi(
    new Configuration({
      basePath: ADMIN_SERVICE_API_URL,
    })
  );
  const { returnUndefinedOnStatus204OrElseDate } = useApiUtils();

  const { mapGroupedWahltagDtosToWahltage } = useWahltagMapper();
  const { addNotification } = useUserNotificationService();
  const { groupWahltagDtosByWahltag } = useWahltagDtoUtils();

  async function getWahltage(isLoading?: Ref<boolean>): Promise<Wahltag[]> {
    updateLoading(true, isLoading);

    const result: Wahltag[] = [];
    try {
      const wahltagDtos = await adminWahltageAPI
        .getWahltage()
        .then((response) => returnUndefinedOnStatus204OrElseDate(response));

      if (wahltagDtos) {
        const wahltageGroupByDatum = groupWahltagDtosByWahltag(wahltagDtos);
        result.push(...mapGroupedWahltagDtosToWahltage(wahltageGroupByDatum));
        result.forEach((wahltag) => wahltag.events.sort(compareByNummerAsc));
      }
    } catch {
      addNotification("Wahltage konnten nicht geladen werden", "Error");
    }

    updateLoading(false, isLoading);

    return result;
  }

  async function isKonfigurierterWahltag(wahltagID: string): Promise<boolean> {
    try {
      const konfigurierteWahltage = await adminKonfigurierteWahltageAPI
        .getKonfigurierteWahltage()
        .then((response) => response.data);

      return konfigurierteWahltage.some(
        (konfigurierterWahltag) => konfigurierterWahltag.wahltagID === wahltagID
      );
    } catch {
      addNotification(
        "Abrufen der konfigurierten Wahltage fehlgeschlagen",
        "Error"
      );
    }

    return false;
  }

  return {
    getWahltage,
    isKonfigurierterWahltag,
  };
}

function updateLoading(loadingState: boolean, loadingRef?: Ref<boolean>) {
  if (loadingRef) {
    loadingRef.value = loadingState;
  }
}
