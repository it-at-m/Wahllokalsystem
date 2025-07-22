import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const briefwahlService = useBriefwahlService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const { currentUserWahltagID, currentUserWahlbezirkID } =
    storeToRefs(useUserStore());
  const wahlen = ref<Wahl[] | null>();

  const waehlerverzeichnisNummern = computed<number[]>(() => {
    if (!wahlen.value) return [];

    const nummern = new Set<number>();

    for (const wahl of wahlen.value) {
      nummern.add(wahl.waehlerverzeichnisNummer);
    }
    return Array.from(nummern);
  });

  async function initWahlen(sendNotification = true) {
    wahlen.value = await wahlenService.getWahlen(
      currentUserWahltagID.value,
      sendNotification
    );
  }

  async function initBeanstandeteWahlbriefe(waehlerverzeichnisNummer: number) {
    const beanstandeteWahlbriefe =
      await briefwahlService.getBeanstandeteWahlbriefe(
        waehlerverzeichnisNummer,
        currentUserWahlbezirkID.value
      );
    if (wahlen.value && beanstandeteWahlbriefe) {
      for (const wahl of wahlen.value) {
        wahl.beanstandeteWahlbriefe =
          beanstandeteWahlbriefe.beanstandeteWahlbriefe.get(wahl.wahlID) ?? [];
      }
    }
  }

  function getWaehlerverzeichnisOrUndefinedById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.waehlerverzeichnisNummer : undefined;
  }

  function getWahlNameOrBlankStringById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.name : "";
  }

  function getWahlTagOrBlankStringById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.wahltag : "";
  }

  function _getWahlOrUndefinedById(wahlID: string) {
    return wahlen.value
      ? wahlen.value.find((wahl) => wahl.wahlID === wahlID)
      : undefined;
  }

  return {
    wahlen,
    getWaehlerverzeichnisOrUndefinedById,
    waehlerverzeichnisNummern,
    getWahlNameOrBlankStringById,
    getWahlTagOrBlankStringById,
    initWahlen,
    initBeanstandeteWahlbriefe,
  };
});

registerStoreHMR(useWahlenStore);
