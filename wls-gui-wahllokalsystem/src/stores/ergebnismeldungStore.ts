import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getErgebnisse } = useErgebnisService();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const ergebnisse = ref<Ergebnisse | null>(null);

  async function loadErgebnisseByStapelArt(
    wahlID: string,
    wahlbezirkID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      ergebnisse.value = await getErgebnisse(wahlID, wahlbezirkID, stapelArt);
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  return { ergebnisse, loadErgebnisseByStapelArt };
});

registerStoreHMR(useErgebnismeldungStore);
