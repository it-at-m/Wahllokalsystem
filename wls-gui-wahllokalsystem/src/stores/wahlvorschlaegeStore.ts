import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getWahlvorschlaege } = useWahlvorschlaegeService();

const storeID = "wahlvorschlaege";

export const useWahlvorschlaegeStore = defineStore(storeID, () => {
  const wahlvorschlaege = ref<Wahlvorschlaege[]>([]);

  async function loadWahlvorschlaege(wahlID: string, wahlbezirkID: string) {
    try {
      const loadedWahlvorschlaege = await getWahlvorschlaege(
        wahlID,
        wahlbezirkID
      );

      wahlvorschlaege.value.push(loadedWahlvorschlaege);
    } catch {
      throw new Error("Fehler beim Laden der Wahlvorschläge");
    }
    _sortWahlvorschlaegeByOrdnungszahl();
  }

  function _sortWahlvorschlaegeByOrdnungszahl() {
    wahlvorschlaege.value.forEach((wahlvorschlag) => {
      const vorschlaegeSet = wahlvorschlag.wahlvorschlaege;

      const sortedArray = Array.from(vorschlaegeSet).sort(
        (vorschlagA, vorschlagB) =>
          vorschlagA.ordnungszahl - vorschlagB.ordnungszahl
      );

      wahlvorschlag.wahlvorschlaege = new Set(sortedArray);
    });
  }

  return {
    wahlvorschlaege,
    loadWahlvorschlaege,
  };
});

registerStoreHMR(useWahlvorschlaegeStore);
