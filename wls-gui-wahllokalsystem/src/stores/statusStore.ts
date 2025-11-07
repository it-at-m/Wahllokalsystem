import type { Status } from "@/types/ergebnismeldung/Status.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useStatusService } from "@/composables/ergebnismeldung/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

export const storeID = "status";

export const useStatusStore = defineStore(storeID, () => {
  const { getStatus } = useStatusService();

  const status = ref<Status[]>([]);

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

  return {
    status,
    loadStatus,
  };
});

const { registerStoreHMR } = useHmrUpdate();
registerStoreHMR(useStatusStore);
