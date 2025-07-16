import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

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
          beanstandeteWahlbriefe.beanstandeteWahlbriefe.get(wahl.wahlID) ??
          undefined;
      }
    }
  }

  function getWahlNameOrBlankStringById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.name : "";
  }

  function getWahlTagOrBlankStringById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.wahltag : "";
  }

  function getWaehlerverzeichnisnummerOrUndefinedById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.waehlerverzeichnisNummer : undefined;
  }

  function getWahlOrUndefinedById(wahlID: string) {
    return wahlen.value
      ? wahlen.value.find((wahl) => wahl.wahlID === wahlID)
      : undefined;
  }

  return {
    wahlen,
    getWahlOrUndefinedById,
    getWahlNameOrBlankStringById,
    getWahlTagOrBlankStringById,
    getWaehlerverzeichnisnummerOrUndefinedById,
    initWahlen,
    initBeanstandeteWahlbriefe,
  };
});

registerStoreHMR(useWahlenStore);
