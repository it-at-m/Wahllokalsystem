import { ref } from "vue";

import {
  Configuration,
  WahllokalBenutzerControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const wahllokalBenutzerAPI = new WahllokalBenutzerControllerApi(
  new Configuration({
    basePath: ADMIN_SERVICE_API_URL,
  })
);
const { addNotification } = useUserNotificationService();

export function useWahllokalBenutzerService() {
  const isGenerating = ref(false);
  const isExporting = ref(false);
  const isDeleting = ref(false);

  async function generateBenutzer(wahltagID: string) {
    isGenerating.value = true;
    try {
      await wahllokalBenutzerAPI.generateWahllokalbenutzer(wahltagID);
      addNotification(
        "Wahllokalbenutzer wurden erstellt",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      addNotification(
        "Erstellen der Wahllokalbenutzer fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isGenerating.value = false;
    }
  }

  async function exportBenutzer(wahltagID: string) {
    isExporting.value = true;
    try {
      const response =
        await wahllokalBenutzerAPI.exportWahllokalBenutzer(wahltagID);
      // Die OpenAPI-Spezifikation deklariert ein Array, die Backend-Implementierung
      // kann jedoch ein einzelnes DTO liefern -> defensiv auf ein Array normalisieren.
      const csvFiles = Array.isArray(response.data)
        ? response.data
        : [response.data];
      downloadCsv(csvFiles.map((csvFile) => csvFile.csv).join("\n"), wahltagID);
    } catch (error) {
      addNotification(
        "Export der Wahllokalbenutzer fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isExporting.value = false;
    }
  }

  async function deleteBenutzer(wahltagID: string) {
    isDeleting.value = true;
    try {
      await wahllokalBenutzerAPI.deleteWahllokalBenutzer(wahltagID);
      addNotification(
        "Wahllokalbenutzer wurden gelöscht",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      addNotification(
        "Löschen der Wahllokalbenutzer fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isDeleting.value = false;
    }
  }

  function downloadCsv(csvContent: string, wahltagID: string) {
    let url: string | null = null;
    try {
      url = window.URL.createObjectURL(
        new Blob([csvContent], { type: "text/csv" })
      );

      const link = document.createElement("a");
      link.href = url;
      link.download = `wahllokalbenutzer-${wahltagID}.csv`;

      document.body.appendChild(link);
      link.click();
      link.remove();
    } finally {
      if (url) {
        window.URL.revokeObjectURL(url);
      }
    }
  }

  return {
    deleteBenutzer,
    exportBenutzer,
    generateBenutzer,
    isDeleting,
    isExporting,
    isGenerating,
  };
}
