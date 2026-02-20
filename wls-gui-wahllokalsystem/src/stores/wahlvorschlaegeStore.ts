import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getWahlvorschlaege } = useWahlvorschlaegeService();
const { sortWahlvorschlaegeByOrdnungszahl } = useWahlvorschlagUtils();

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

    wahlvorschlaege.value.forEach((wahlvorschlaege) =>
      sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege)
    );
  }

  function getWahlvorschlaegeByWahlIDAndWahlbezirkID(
    wahlID: string,
    wahlbezirkID: string
  ): Wahlvorschlaege | undefined {
    return wahlvorschlaege.value.find(
      (wahlvorschlaege) =>
        wahlvorschlaege.wahlID === wahlID &&
        wahlvorschlaege.wahlbezirkID === wahlbezirkID
    );
  }

  function getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
    wahlID: string,
    wahlbezirkID: string,
    wahlvorschlagID: string
  ): Wahlvorschlag | undefined {
    const wahlvorschlaege = getWahlvorschlaegeByWahlIDAndWahlbezirkID(
      wahlID,
      wahlbezirkID
    )?.wahlvorschlaege;

    if (!wahlvorschlaege) {
      return undefined;
    }

    return [...wahlvorschlaege].find(
      (wahlvorschlag) => wahlvorschlag.identifikator === wahlvorschlagID
    );
  }

  return {
    wahlvorschlaege,
    getWahlvorschlaegeByWahlIDAndWahlbezirkID,
    getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID,
    loadWahlvorschlaege,
  };
});

registerStoreHMR(useWahlvorschlaegeStore);
