import type { Status } from "@/types/ergebnismeldung/Status.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useStatusService } from "@/composables/ergebnismeldung/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getStatus } = useStatusService();

export const storeID = "status";

export const useStatusStore = defineStore(storeID, () => {
  const status = ref<Status[]>([]);

  const DEFAULT_MELDUNG = {
    validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
    gedruckt: false,
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
      console.debug("StatusForWahl: ", statusForWahl);
      if (statusForWahl) {
        status.value.push(statusForWahl);
      } else {
        console.debug("Push: ", {
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          schnellmeldung: DEFAULT_MELDUNG,
          niederschrift: DEFAULT_MELDUNG,
        });
        status.value.push({
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          schnellmeldung: DEFAULT_MELDUNG,
          niederschrift: DEFAULT_MELDUNG,
        });
        console.debug("danach: ", status.value);
      }
    } catch {
      throw Error(`Fehler beim Laden des Status für WahlID: ${wahlID}`);
    }
  }

  return {
    loadStatus,
  };
});

registerStoreHMR(useStatusStore);
