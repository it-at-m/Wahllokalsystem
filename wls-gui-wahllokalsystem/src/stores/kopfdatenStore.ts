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
    try {
      const loadedDataAsPromises = currentWahlMetadata.value.map((metadata) =>
        kopfdatenService.getKopfdaten(metadata.wahlID, metadata.wahlbezirkID)
      );
      kopfdaten.value = await Promise.all(loadedDataAsPromises);
    } catch {
      throw Error("Fehler beim Resolven der Promises");
    }
  }

  return {
    initKopfdaten,
  };
});
