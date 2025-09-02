import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getErgebnisse } = useErgebnisService();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());

  const ergebnisse = ref<Ergebnisse | null>(null);

  async function loadErgebnisseByStapelArt(
    wahlID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      const wahlbezirkID = _getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

      if (wahlbezirkID) {
        ergebnisse.value = await getErgebnisse(wahlbezirkID, wahlID, stapelArt);
      }
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  function _getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID: string) {
    const metadata = currentUserWahlMetadata.value.find((metadata) => {
      return metadata.wahlID === wahlID;
    });

    return metadata?.wahlbezirkID;
  }

  return { ergebnisse, loadErgebnisseByStapelArt };
});

registerStoreHMR(useErgebnismeldungStore);
