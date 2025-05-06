import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useWahlService } from "@/composables/wahlen/wahlenService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const userStore = useUserStore();
export const useWahlenStore = defineStore(storeID, () => {
  const wahlen = ref<Wahl[]>();
  const wahlenReady = ref(false);

  async function loadWahlen() {
    const currentWahltag = userStore.currentUserWahltagID;
    if (currentWahltag) {
      await wahlenService.loadWahlen(currentWahltag);
      wahlenReady.value = true;
    }
  }

  return {
    wahlen,
    wahlenReady,
    loadWahlen,
  };
});
