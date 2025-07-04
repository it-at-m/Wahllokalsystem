import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const { currentUserWahltagID } = storeToRefs(useUserStore());
  const wahlen = ref<Wahl[] | null>();

  async function initWahlen(sendNotification = true) {
    wahlen.value = await wahlenService.getWahlen(
      currentUserWahltagID.value,
      sendNotification
    );
  }

  function getWahlNameOrBlankStringById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.name : "";
  }

  function getWahlTagOrBlankStringById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.wahltag : "";
  }

  function getWaehlerverzeichnisnummerOrUndefinedById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.waehlerverzeichnisNummer : undefined;
  }

  function _getWahlOrUndefinedById(wahlID: string) {
    return wahlen.value
      ? wahlen.value.find((wahl) => wahl.wahlID === wahlID)
      : undefined;
  }

  return {
    wahlen,
    getWahlNameOrBlankStringById,
    getWahlTagOrBlankStringById,
    getWaehlerverzeichnisnummerOrUndefinedById,
    initWahlen,
  };
});

registerStoreHMR(useWahlenStore);
