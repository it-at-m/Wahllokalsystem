import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
  const { getErgebnisse, postErgebnisse } = useErgebnisService();

  const ergebnisse = ref<Ergebnisse[]>([]);
  const isErgebnisseSaving = ref<boolean>(false);

  function deleteErgebnisseWithNumIndexAbove(
    ergebnisseWahlID: string,
    ergebnisseStapelArt: StapelArtEnum,
    highestAllowedNumIndex: number
  ) {
    const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
      ergebnisseWahlID,
      ergebnisseStapelArt
    );
    if (ergebnisseFound) {
      ergebnisseFound.ergebnisse = ergebnisseFound.ergebnisse.filter(
        (ergebnis) => (ergebnis.numIndex || 0) <= highestAllowedNumIndex
      );
    }
  }

  async function loadErgebnisseByStapelArt(
    wahlID: string,
    stapelArt: StapelArtEnum,
    sendNotification = true
  ) {
    try {
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

      if (wahlbezirkID) {
        const loadedErgebnisse = await getErgebnisse(
          wahlbezirkID,
          wahlID,
          stapelArt,
          sendNotification
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
    stapelArt: StapelArtEnum,
    sendNotification = true
  ) {
    try {
      isErgebnisseSaving.value = true;
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);
      const ergebnisseToSend = getErgebnisseByWahlIdAndStapelartOrUndefined(
        wahlID,
        stapelArt
      );

      if (
        wahlbezirkID &&
        ergebnisseToSend &&
        ergebnisseToSend.ergebnisse.length > 0
      ) {
        await postErgebnisse(
          wahlbezirkID,
          wahlID,
          stapelArt,
          ergebnisseToSend,
          sendNotification
        );
      }
    } catch {
      throw new Error("Fehler beim Speichern der Ergebnisse");
    } finally {
      isErgebnisseSaving.value = false;
    }
  }

  function switchStapelOfErgebnis(
    key: BezirkUndWahlIDStapelArt,
    numIndex: number,
    targetStapelArt: StapelArtEnum
  ) {
    const sourceErgebnisse = getErgebnisseAndCreateIfMissing(key);
    const targetErgebnisse = getErgebnisseAndCreateIfMissing({
      ...key,
      stapelArt: targetStapelArt,
    });

    const indexOfErgebnisToMove = sourceErgebnisse.ergebnisse.findIndex(
      (ergebnis) => ergebnis.numIndex === numIndex
    );
    if (indexOfErgebnisToMove >= 0) {
      targetErgebnisse.ergebnisse.push(
        ...sourceErgebnisse.ergebnisse.splice(indexOfErgebnisToMove, 1)
      );
    }
  }

  function getErgebnisseByWahlIdAndStapelartOrUndefined(
    wahlID: string,
    stapelArt: StapelArtEnum
  ): Ergebnisse | undefined {
    return ergebnisse.value.find(
      (ergebnisse) =>
        ergebnisse.bezirkUndWahlIDStapelart.stapelArt === stapelArt &&
        ergebnisse.bezirkUndWahlIDStapelart.wahlID === wahlID
    );
  }

  function findAndUpdateErgebnisseByWahlIdAndStapelArt(
    wahlID: string,
    stapelArt: StapelArtEnum,
    ergebnisList: Ergebnis[]
  ) {
    const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID,
      stapelArt
    );
    if (ergebnisseFound) {
      ergebnisseFound.ergebnisse = ergebnisList;
    } else {
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

      if (wahlbezirkID) {
        const newErgebnisseToAdd: Ergebnisse = {
          bezirkUndWahlIDStapelart: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
            stapelArt: stapelArt,
          },
          ergebnisse: ergebnisList,
        };
        ergebnisse.value.push(newErgebnisseToAdd);
      }
    }
  }

  function getErgebnisseAndCreateIfMissing(key: BezirkUndWahlIDStapelArt) {
    let ergebnisseForKey = getErgebnisseByWahlIdAndStapelartOrUndefined(
      key.wahlID,
      key.stapelArt
    );
    if (!ergebnisseForKey) {
      ergebnisseForKey = {
        bezirkUndWahlIDStapelart: key,
        ergebnisse: [],
      };
      ergebnisse.value.push(ergebnisseForKey);
    }
    return ergebnisseForKey;
  }

  return {
    ergebnisse,
    deleteErgebnisseWithNumIndexAbove,
    isErgebnisseSaving,
    getErgebnisseByWahlIdAndStapelartOrUndefined,
    getErgebnisseAndCreateIfMissing,
    findAndUpdateErgebnisseByWahlIdAndStapelArt,
    loadErgebnisseByStapelArt,
    sendErgebnisseByStapelArt,
    switchStapelOfErgebnis,
  };
});

registerStoreHMR(useErgebnismeldungStore);
