import type { Begruendung } from "@/types/ergebnisermittlung/Begruendung.ts";
import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useErgebnisermittlungService } from "@/composables/ergebnisermittlung/ergebnisermittlungService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "ergebnismeldung";

export const useErgebnismeldungStore = defineStore(storeID, () => {
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
  const { getErgebnisse, postErgebnisse } = useErgebnisService();
  const { getBegruendungStimmzettelumschlaege, postBegruendung } =
    useErgebnisermittlungService();
  const { getStimmzettelTermForWahl } = useTextFormatter();

  const ergebnisse = ref<Ergebnisse[]>([]);
  const isErgebnisseSaving = ref<boolean>(false);
  const begruendungen = ref<Begruendung[]>([]);
  const isBegruendungSaving = ref<boolean>(false);

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

      if (wahlbezirkID && ergebnisseToSend) {
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

  async function loadBegruendungForWahl(wahl: Wahl, sendNotification = true) {
    try {
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahl.wahlID);

      if (wahlbezirkID) {
        const loadedBegruendung = await getBegruendungStimmzettelumschlaege(
          wahl,
          wahlbezirkID,
          getStimmzettelTermForWahl(wahl),
          sendNotification
        );
        if (loadedBegruendung) {
          const existingBegruendungIndexForWahl = begruendungen.value.findIndex(
            (begruendung) => begruendung.wahlID === wahl.wahlID
          );
          if (existingBegruendungIndexForWahl >= 0) {
            begruendungen.value[existingBegruendungIndexForWahl] =
              loadedBegruendung;
          } else {
            begruendungen.value.push(loadedBegruendung);
          }
        }
      }
    } catch (e) {
      throw new Error(
        `Fehler beim Laden der Begründung für ${wahl.name}. ` + e
      );
    }
  }

  async function saveBegruendung(
    begruendung: Begruendung,
    sendNotification = true
  ) {
    try {
      isBegruendungSaving.value = true;
      const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(
        begruendung.wahlID
      );
      if (wahlbezirkID) {
        const existingBegruendungIndex = begruendungen.value.findIndex(
          (b) => b.wahlID === begruendung.wahlID
        );
        if (existingBegruendungIndex >= 0) {
          begruendungen.value[existingBegruendungIndex] = begruendung;
        } else {
          begruendungen.value.push(begruendung);
        }
        await postBegruendung(begruendung, wahlbezirkID, sendNotification);
      }
    } catch {
      throw new Error(`Fehler beim Speichern der Begründung.`);
    } finally {
      isBegruendungSaving.value = false;
    }
  }

  return {
    ergebnisse,
    begruendungen,
    deleteErgebnisseWithNumIndexAbove,
    isErgebnisseSaving,
    isBegruendungSaving,
    getErgebnisseByWahlIdAndStapelartOrUndefined,
    getErgebnisseAndCreateIfMissing,
    findAndUpdateErgebnisseByWahlIdAndStapelArt,
    loadErgebnisseByStapelArt,
    sendErgebnisseByStapelArt,
    switchStapelOfErgebnis,
    loadBegruendungForWahl,
    saveBegruendung,
  };
});

registerStoreHMR(useErgebnismeldungStore);
