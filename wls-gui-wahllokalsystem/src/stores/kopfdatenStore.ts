import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useKopfdatenService } from "@/composables/kopfdaten/kopfdatenService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const kopfdatenService = useKopfdatenService();

export const useKopfdatenStore = defineStore("kopfdaten", () => {
  const kopfdaten = ref<Kopfdaten[]>([]);
  const { currentWahlMetadata } = storeToRefs(useUserStore());

  async function initKopfdaten() {
    kopfdaten.value = [];
    for (const metadata of currentWahlMetadata.value) {
      const loadedKopfdaten = await kopfdatenService.getKopfdaten(
        metadata.wahlID,
        metadata.wahlbezirkID
      );
      kopfdaten.value.push(loadedKopfdaten);
    }
  }

  return {
    initKopfdaten,
  };
});
