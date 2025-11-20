import type { Status } from "@/types/ergebnismeldung/Status.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useStatusService } from "@/composables/ergebnismeldung/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

export const storeID = "status";

export const useStatusStore = defineStore(storeID, () => {
  const { getStatus, postStatus } = useStatusService();

  const status = ref<Status[]>([]);
  const isStatusSaving = ref(false);

  const DEFAULT_MELDUNG = {
    validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
    gedruckt: false,
    uebermittelt: undefined,
    sendeuhrzeit: undefined,
  };

  async function loadStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const statusForWahl = await getStatus(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      if (statusForWahl) {
        status.value.push(statusForWahl);
      } else {
        status.value.push({
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          schnellmeldung: DEFAULT_MELDUNG,
          niederschrift: DEFAULT_MELDUNG,
        });
      }
    } catch {
      throw Error(`Fehler beim Laden des Status für WahlID: ${wahlID}`);
    }
  }

  async function saveStatus(wahlID: string, wahlbezirkID: string) {
    isStatusSaving.value = true;
    for (const statusEntry of status.value) {
      try {
        await postStatus(wahlID, wahlbezirkID, statusEntry);
      } catch {
        throw Error(`Fehler beim Speichern des Status für WahlID: ${wahlID}`);
      } finally {
        isStatusSaving.value = false;
      }
    }
  }

  return {
    status,
    loadStatus,
    saveStatus,
  };
});

const { registerStoreHMR } = useHmrUpdate();
registerStoreHMR(useStatusStore);
