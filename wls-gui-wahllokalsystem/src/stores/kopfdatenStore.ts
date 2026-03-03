import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useKopfdatenService } from "@/composables/kopfdaten/kopfdatenService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { registerStoreHMR } = useHmrUpdate();
const kopfdatenService = useKopfdatenService();

export const useKopfdatenStore = defineStore("kopfdaten", () => {
  const kopfdaten = ref<Kopfdaten[]>([]);
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());

  async function initKopfdaten() {
    try {
      const loadedDataAsPromises = currentUserWahlMetadata.value.map(
        (metadata) =>
          kopfdatenService.getKopfdaten(metadata.wahlID, metadata.wahlbezirkID)
      );
      kopfdaten.value = await Promise.all(loadedDataAsPromises);
    } catch {
      throw Error("Fehler beim Resolven der Promises");
    }
  }

  async function loadKopfdaten(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const loadedKopfdaten = await kopfdatenService.getKopfdaten(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      kopfdaten.value.push(loadedKopfdaten);
    } catch {
      throw Error("Fehler beim Resolven der Promises");
    }
  }

  return {
    initKopfdaten,
    kopfdaten,
    loadKopfdaten,
  };
});

registerStoreHMR(useKopfdatenStore);
