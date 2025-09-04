import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
  const { getErgebnisse, postErgebnisse } = useErgebnisService();

  const ergebnisse = ref<Ergebnisse[]>([]);

  async function loadErgebnisseByStapelArt(
    wahlID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

      if (wahlbezirkID) {
        const loadedErgebnisse = await getErgebnisse(
          wahlbezirkID,
          wahlID,
          stapelArt
        );
        if (loadedErgebnisse) {
          const existingErgebnisseIndexForStapelart =
            ergebnisse.value.findIndex(
              (ergebnisse) =>
                ergebnisse.bezirkUndWahlIDStapelart.wahlID === wahlID &&
                ergebnisse.bezirkUndWahlIDStapelart.stapelArt === stapelArt
            );
          if (existingErgebnisseIndexForStapelart >= 0) {
            ergebnisse.value[existingErgebnisseIndexForStapelart] =
              loadedErgebnisse;
          } else {
            ergebnisse.value.push(loadedErgebnisse);
          }
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
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);
      const ergebnisseToSend = _getErgebnisseByWahlIdAndStapelartOrUndefined(
        wahlID,
        stapelArt
      );

      if (
        wahlbezirkID &&
        ergebnisseToSend &&
        ergebnisseToSend.ergebnisse.length > 0
      ) {
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

  return { ergebnisse, loadErgebnisseByStapelArt, sendErgebnisseByStapelArt };
});

registerStoreHMR(useErgebnismeldungStore);
