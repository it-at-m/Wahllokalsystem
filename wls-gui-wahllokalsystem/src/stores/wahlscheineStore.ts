import type { Wahlscheine } from "@/types/ereignismeldung/Wahlscheine.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlscheineService } from "@/composables/ergebnismeldung/wahlscheineService.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getWahlscheine } = useWahlscheineService();

export const useWahlscheineStore = defineStore("ergebnismeldung", () => {
  const wahlscheine = ref<Wahlscheine[]>([]);

  async function loadWahlscheine(wahlID: string, wahlbezirkID: string) {
    try {
      const wahlschein = await getWahlscheine(wahlID, wahlbezirkID);
      if (wahlschein) {
        wahlscheine.value.push(wahlschein);
      }
    } catch {
      throw Error("Fehler beim Resolven des Promises");
    }
  }

  return { loadWahlscheine };
});

registerStoreHMR(useWahlscheineStore);
