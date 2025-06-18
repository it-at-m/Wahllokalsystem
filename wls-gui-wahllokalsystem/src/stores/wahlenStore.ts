import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();

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

  return {
    wahlen,
    initWahlen,
  };
});
