import type { CsvFileDTO } from "@/api/wls-clients/generated-admin-api";

import { ref } from "vue";

import {
  Configuration,
  WahllokalBenutzerControllerApi,
} from "@/api/wls-clients/generated-admin-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ADMIN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export interface WahllokalBenutzer {
  benutzername: string;
}

const adminWahllokalBenutzerAPI = new WahllokalBenutzerControllerApi(
  new Configuration({
    basePath: ADMIN_SERVICE_API_URL,
  })
);
const { addNotification } = useUserNotificationService();

export function useWahllokalBenutzerService() {
  const benutzer = ref<WahllokalBenutzer[]>([]);
  const isLoading = ref(false);
  const isGenerating = ref(false);
  const isExporting = ref(false);
  const isDeleting = ref(false);

  async function loadBenutzer(wahltagID: string) {
    isLoading.value = true;
    try {
      const response =
        await adminWahllokalBenutzerAPI.exportWahllokalBenutzer(wahltagID);
      benutzer.value = parseBenutzer(toCsvFileDTOArray(response.data));
    } catch (error) {
      addNotification(
        "Laden der Wahllokalbenutzer fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  async function generateBenutzer(wahltagID: string) {
    isGenerating.value = true;
    try {
      const response =
        await adminWahllokalBenutzerAPI.generateWahllokalbenutzer(wahltagID);
      benutzer.value = parseBenutzer([response.data]);
      downloadCsvFiles([response.data], wahltagID);
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
        await adminWahllokalBenutzerAPI.exportWahllokalBenutzer(wahltagID);
      downloadCsvFiles(toCsvFileDTOArray(response.data), wahltagID);
      addNotification(
        "Wahllokalbenutzer wurden exportiert",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      addNotification(
        "Exportieren der Wahllokalbenutzer fehlgeschlagen",
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
      await adminWahllokalBenutzerAPI.deleteWahllokalBenutzer(wahltagID);
      benutzer.value = [];
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

  return {
    benutzer,
    clearBenutzer,
    deleteBenutzer,
    exportBenutzer,
    generateBenutzer,
    isDeleting,
    isExporting,
    isGenerating,
    isLoading,
    loadBenutzer,
  };

  function clearBenutzer() {
    benutzer.value = [];
  }
}

function toCsvFileDTOArray(
  csvFileDTOOrArray: CsvFileDTO | CsvFileDTO[]
): CsvFileDTO[] {
  return Array.isArray(csvFileDTOOrArray)
    ? csvFileDTOOrArray
    : [csvFileDTOOrArray];
}

function downloadCsvFiles(csvFileDTOs: CsvFileDTO[], wahltagID: string) {
  if (csvFileDTOs.length === 0) {
    throw new Error("No CSV data returned");
  }

  csvFileDTOs.forEach((csvFileDTO, index) => {
    downloadCsvFile(
      csvFileDTO.csv,
      `wahllokalbenutzer-${wahltagID}${csvFileDTOs.length > 1 ? `-${index + 1}` : ""}.csv`
    );
  });
}

function parseBenutzer(csvFileDTOs: CsvFileDTO[]): WahllokalBenutzer[] {
  return csvFileDTOs
    .flatMap((csvFileDTO) => csvFileDTO.csv.split(/\r?\n/))
    .map((csvLine) => csvLine.trim().replace(/^\uFEFF/, ""))
    .filter((csvLine) => csvLine.length > 0)
    .map((csvLine) => ({ benutzername: csvLine }))
    .sort((firstBenutzer, secondBenutzer) =>
      firstBenutzer.benutzername.localeCompare(secondBenutzer.benutzername)
    );
}

function downloadCsvFile(csv: string, filename: string) {
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}
