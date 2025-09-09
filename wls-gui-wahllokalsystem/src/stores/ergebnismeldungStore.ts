import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
  const { getErgebnisse, postErgebnisse } = useErgebnisService();
  const wahlvorschlaegeStore = useWahlvorschlaegeStore();

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
          _addOrUpdateErgebnisse(loadedErgebnisse);
        } else {
          _initErgebnisse(wahlID, wahlbezirkID, stapelArt);
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
      const ergebnisseToSend = getErgebnisseByWahlIdAndStapelartOrUndefined(
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

  function _addOrUpdateErgebnisse(ergebnisseToSet: Ergebnisse) {
    const existingErgebnisseIndexForStapelart = ergebnisse.value.findIndex(
      (ergebnisse) =>
        ergebnisse.bezirkUndWahlIDStapelart.wahlID ===
          ergebnisseToSet.bezirkUndWahlIDStapelart.wahlID &&
        ergebnisse.bezirkUndWahlIDStapelart.stapelArt ===
          ergebnisseToSet.bezirkUndWahlIDStapelart.stapelArt
    );
    if (existingErgebnisseIndexForStapelart >= 0) {
      ergebnisse.value[existingErgebnisseIndexForStapelart] = ergebnisseToSet;
    } else {
      ergebnisse.value.push(ergebnisseToSet);
    }
  }

  function _initErgebnisse(
    wahlID: string,
    wahlbezirkID: string,
    stapelArt: StapelArtEnum
  ) {
    const wahlvorschlaege =
      wahlvorschlaegeStore.getWahlvorschlaegeByWahlIDAndWahlbezirkID(
        wahlID,
        wahlbezirkID
      );
    if (wahlvorschlaege) {
      _initErgebnisseForWahlvorschlaege(wahlvorschlaege, stapelArt);
    }
  }

  function _initErgebnisseForWahlvorschlaege(
    wahlvorschlaege: Wahlvorschlaege,
    stapel: StapelArtEnum
  ) {
    const key = {
      wahlbezirkID: wahlvorschlaege.wahlbezirkID,
      wahlID: wahlvorschlaege.wahlID,
      stapelArt: stapel,
    };
    const result: Ergebnisse = {
      bezirkUndWahlIDStapelart: key,
      ergebnisse: [],
    };
    [...wahlvorschlaege.wahlvorschlaege].forEach((wahlvorschlag, index) => {
      result.ergebnisse.push({
        numIndex: index + 1,
        wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
        ergebnis: null,
        kandidatID: null,
        wahlvorschlagID: wahlvorschlag.identifikator,
      });
    });
    _addOrUpdateErgebnisse(result);
  }

  return {
    ergebnisse,
    getErgebnisseByWahlIdAndStapelartOrUndefined,
    loadErgebnisseByStapelArt,
    sendErgebnisseByStapelArt,
  };
});

registerStoreHMR(useErgebnismeldungStore);
