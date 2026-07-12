import type { WahlDTO } from "@/api/wls-clients/generated-basisdaten-api";

import { ref } from "vue";

import {
  Configuration,
  WahlenControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useApiUtils } from "@/composables/common/apiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const wahlenAPI = new WahlenControllerApi(
  new Configuration({
    basePath: BASISDATEN_SERVICE_API_URL,
  })
);
const { returnUndefinedOnStatus204OrElseResponseData } = useApiUtils();
const { addNotification } = useUserNotificationService();

export function useWahlenService() {
  const isLoading = ref(false);
  const isSaving = ref(false);

  async function getWahlen(wahltagID: string): Promise<WahlDTO[]> {
    isLoading.value = true;
    try {
      const wahlen = await wahlenAPI
        .getWahlen(wahltagID)
        .then((response) =>
          returnUndefinedOnStatus204OrElseResponseData(response)
        );
      return wahlen ?? [];
    } catch (error) {
      addNotification(
        "Laden der Wahlen fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  async function updateWahlen(
    wahltagID: string,
    wahlen: WahlDTO[]
  ): Promise<void> {
    isSaving.value = true;
    try {
      await wahlenAPI.postWahlen(wahltagID, wahlen);
      addNotification(
        "Wahlen wurden gespeichert",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      addNotification(
        "Speichern der Wahlen fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isSaving.value = false;
    }
  }

  return {
    getWahlen,
    isLoading,
    isSaving,
    updateWahlen,
  };
}
