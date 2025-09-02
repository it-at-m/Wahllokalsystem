import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());
  const { getErgebnisse, postErgebnisse } = useErgebnisService();

  const ergebnisse = ref<Ergebnisse[]>([]);

  async function loadErgebnisseByStapelArt(
    wahlID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      const wahlbezirkID = _getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

      if (wahlbezirkID) {
        const loadedErgebnisse = await getErgebnisse(
          wahlbezirkID,
          wahlID,
          stapelArt
        );
        if (loadedErgebnisse) {
          ergebnisse.value.push(loadedErgebnisse);
        }
      }
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  async function sendErgebnisseByStapelArt(
    wahlID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      const wahlbezirkID = _getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);
      const ergebnisseToSend = _getErgebnisseByWahlIdAndStapelartOrUndefined(
        wahlID,
        stapelArt
      );

      if (wahlbezirkID && ergebnisseToSend) {
        await postErgebnisse(wahlbezirkID, wahlID, stapelArt, ergebnisseToSend);
      }
    } catch {
      throw new Error("Fehler beim Speichern der Ergebnisse");
    }
  }

  function _getErgebnisseByWahlIdAndStapelartOrUndefined(
    wahlID: string,
    stapelArt: StapelArtEnum
  ): Ergebnisse | undefined {
    return ergebnisse.value.find(
      (ergebnisse) =>
        ergebnisse.bezirkUndWahlIDStapelart.stapelArt === stapelArt &&
        ergebnisse.bezirkUndWahlIDStapelart.wahlID === wahlID
    );
  }

  function _getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID: string) {
    const metadata = currentUserWahlMetadata.value.find((metadata) => {
      return metadata.wahlID === wahlID;
    });

    return metadata?.wahlbezirkID;
  }

  return { ergebnisse, loadErgebnisseByStapelArt, sendErgebnisseByStapelArt };
});

registerStoreHMR(useErgebnismeldungStore);
