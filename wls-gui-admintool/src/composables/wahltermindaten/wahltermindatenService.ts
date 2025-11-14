import { ref } from "vue";

import {
  Configuration,
  WahltermindatenControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const adminWahltermindatenAPI = new WahltermindatenControllerApi(
  new Configuration({
    basePath: ADMIN_SERVICE_API_URL,
  })
);
const { addNotification } = useUserNotificationService();

export function useWahltermindatenService() {
  const isLoading = ref(false);
  const isDeleting = ref(false);

  async function importWahlterminDaten(wahltagID: string) {
    isLoading.value = true;
    try {
      await adminWahltermindatenAPI.loadWahltermindaten(wahltagID);
    } catch (error) {
      addNotification(
        "Importieren der Wahltermindaten fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  async function deleteAndImportWahlterminDaten(wahltagID: string) {
    try {
      isDeleting.value = true;
      await adminWahltermindatenAPI.deleteWahltermindaten(wahltagID);
      isDeleting.value = false;
      isLoading.value = true;
      await adminWahltermindatenAPI.loadWahltermindaten(wahltagID);
    } catch (error) {
      addNotification(
        "Löschen und Importieren der Wahltermindaten fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isLoading.value = false;
      isDeleting.value = false;
    }
  }

  return {
    deleteAndImportWahlterminDaten,
    isLoading,
    isDeleting,
    importWahlterminDaten,
  };
}
