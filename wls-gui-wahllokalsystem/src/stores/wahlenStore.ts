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
  const wahlenReady = ref(false);

  async function initWahlen(sendNotification = true) {
    const currentWahltag = userStore.currentUserWahltagID;
    if (currentWahltag) {
      wahlen.value = await wahlenService.getWahlen(
        currentWahltag,
        sendNotification
      );
      wahlenReady.value = true;
    } else {
      await Promise.reject();
    }
  }

  return {
    wahlen,
    wahlenReady,
    initWahlen,
  };
});

registerStoreHMR(useWahlenStore);
