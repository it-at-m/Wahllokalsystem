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
    if (wahlen.value) {
      const wahl = wahlen.value.find((wahl) => wahl.wahlID === wahlID);
      return wahl ? wahl.name : "";
    }
    return "";
  }

  return {
    wahlen,
    getWahlNameOrBlankStringById,
    initWahlen,
  };
});

registerStoreHMR(useWahlenStore);
