import { ref } from "vue";

import {
  Configuration,
  WahltermindatenControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";

const controllerApi = new WahltermindatenControllerApi(
  new Configuration({
    basePath: ADMIN_SERVICE_API_URL,
  })
);
const { addNotification } = useUserNotificationService();

export function useWahltermindatenService() {
  const isLoading = ref(false);
  const istDeleting = ref(false);

  async function importWahlterminDaten(wahltagID: string) {
    isLoading.value = true;
    try {
      await controllerApi.loadWahltermindaten(wahltagID);
    } catch {
      addNotification(
        "Importieren der Wahltermindaten fehlgeschlagen",
        "Error"
      );
    }
    isLoading.value = false;
  }

  async function deleteWahltermindaten(wahltagID: string) {
    istDeleting.value = true;

    try {
      await controllerApi.deleteWahltermindaten(wahltagID);
    } catch {
      addNotification("Löschen der Wahltermindaten fehlgeschlagen", "Error");
      return Promise.reject();
    } finally {
      istDeleting.value = false;
    }
  }

  return {
    deleteWahltermindaten,
    isLoading,
    istDeleting,
    importWahlterminDaten,
  };
}
