import type { Wahlscheine } from "@/types/ereignismeldung/Wahlscheine.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlscheineService } from "@/composables/ergebnismeldung/wahlscheineService.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getWahlscheine } = useWahlscheineService();

export const storeID = "wahlscheine";

export const useWahlscheineStore = defineStore(storeID, () => {
  const wahlscheine = ref<Wahlscheine[]>([]);

  async function loadWahlscheine(wahlID: string, wahlbezirkID: string) {
    try {
      const wahlscheineForWahl = await getWahlscheine(wahlID, wahlbezirkID);
      if (wahlscheineForWahl) {
        wahlscheine.value.push(wahlscheineForWahl);
      }
    } catch {
      throw Error(`Fehler beim Laden der Wahlscheine für WahlID: ${wahlID}`);
    }
  }

  return { loadWahlscheine };
});

registerStoreHMR(useWahlscheineStore);
