import type { Status } from "@/types/ergebnismeldung/common/Status.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";

export const storeID = "status";

export const useStatusStore = defineStore(storeID, () => {
  const { postStatus } = useStatusService();
  const { loadStatusByWahlIdAndWahlbezirkId, getInitialStatus } =
    useStatusUtils();

  const status = ref<Status[]>([]);
  const isStatusSaving = ref(false);

  async function loadStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const statusForWahl = await loadStatusByWahlIdAndWahlbezirkId(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      status.value.push(statusForWahl);
    } catch {
      throw Error(`Fehler beim Laden des Status für WahlID: ${wahlID}`);
    }
  }

  async function saveStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    isStatusSaving.value = true;
    try {
      for (const statusEntry of status.value) {
        await postStatus(wahlID, wahlbezirkID, statusEntry, sendNotification);
      }
    } catch {
      throw Error(`Fehler beim Speichern des Status für WahlID: ${wahlID}`);
    } finally {
      isStatusSaving.value = false;
    }
  }

  function getStatusEntry(wahlID: string, wahlbezirkID: string): Status {
    const foundStatus = status.value.find(
      (status) =>
        status.bezirkUndWahlID?.wahlID === wahlID &&
        status.bezirkUndWahlID?.wahlbezirkID === wahlbezirkID
    );

    if (foundStatus) return foundStatus;

    const defaultStatus = getInitialStatus(wahlID, wahlbezirkID);
    status.value.push(defaultStatus);
    return defaultStatus;
  }

  return {
    status,
    isStatusSaving,
    loadStatus,
    saveStatus,
    getStatusEntry,
  };
});

const { registerStoreHMR } = useHmrUpdate();
registerStoreHMR(useStatusStore);
