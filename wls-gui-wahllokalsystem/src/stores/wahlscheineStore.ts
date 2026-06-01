import type { Wahlscheine } from "@/types/ergebnismeldung/common/Wahlscheine.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useWahlscheineService } from "@/composables/ergebnismeldung/common/wahlscheineService.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getWahlscheine, postWahlscheine } = useWahlscheineService();

export const storeID = "wahlscheine";

export const useWahlscheineStore = defineStore(storeID, () => {
  const wahlscheine = ref<Wahlscheine[]>([]);
  const isWahlscheineSaving = ref(false);

  const { logDebug } = useLogging("wahlscheineStore");

  async function loadWahlscheine(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const wahlscheineForWahl = await getWahlscheine(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      if (wahlscheineForWahl) {
        wahlscheine.value.push(wahlscheineForWahl);
      } else {
        wahlscheine.value.push({
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          stimmabgabevermerke: null,
        });
      }
    } catch {
      throw Error(`Fehler beim Laden der Wahlscheine für WahlID: ${wahlID}`);
    }
  }

  async function saveWahlscheine() {
    isWahlscheineSaving.value = true;
    try {
      for (const wahlschein of wahlscheine.value) {
        try {
          await postWahlscheine(
            wahlschein.bezirkUndWahlID.wahlID,
            wahlschein.bezirkUndWahlID.wahlbezirkID,
            wahlschein
          );
        } catch (e) {
          logDebug(
            `Save Wahlschein for wahlbezirkID: ${wahlschein.bezirkUndWahlID.wahlbezirkID} and wahlID: ${wahlschein.bezirkUndWahlID.wahlID} failed`,
            e
          );
        }
      }
    } finally {
      isWahlscheineSaving.value = false;
    }
  }

  return { wahlscheine, isWahlscheineSaving, loadWahlscheine, saveWahlscheine };
});

registerStoreHMR(useWahlscheineStore);
