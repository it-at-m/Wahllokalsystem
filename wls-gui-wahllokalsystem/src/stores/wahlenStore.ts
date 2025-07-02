import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const userStore = useUserStore();
  const wahlen = ref<Wahl[] | null>();

  async function initWahlen(sendNotification = true) {
    const currentWahltag = userStore.currentUserWahltagID;
    if (currentWahltag) {
      wahlen.value = await wahlenService.getWahlen(
        currentWahltag,
        sendNotification
      );
    } else {
      await Promise.reject();
    }
  }

  function getWahlNameOrBlankStringById(wahlID: string) {
    const wahl = _getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.name : "";
  }

  function getWahltagOrBlankById(wahlID: string) {
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
    getWahlNameOrBlankStringById,
    getWahltagOrBlankById,
    initWahlen,
  };
});

registerStoreHMR(useWahlenStore);
