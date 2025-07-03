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
    currentWahlMetadata.value.forEach((metadata) =>
      loadKopfdaten(metadata.wahlID, metadata.wahlbezirkID)
    );
  }

  async function loadKopfdaten(wahlID: string, wahlbezirk: string) {
    const loadedKopfdaten = await kopfdatenService.getKopfdaten(
      wahlID,
      wahlbezirk
    );
    if (loadedKopfdaten) {
      kopfdaten.value.push(loadedKopfdaten);
    }
  }

  return {
    initKopfdaten,
  };
});
