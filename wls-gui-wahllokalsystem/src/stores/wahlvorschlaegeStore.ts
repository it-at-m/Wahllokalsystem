import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { registerStoreHMR } = useHmrUpdate();
const { getWahlvorschlaege } = useWahlvorschlaegeService();

const storeID = "wahlvorschlaege";

export const useWahlvorschlaegeStore = defineStore(storeID, () => {
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());

  const wahlvorschlaege = ref<Wahlvorschlaege | null>(null);

  async function loadWahlvorschlaege(wahlID: string, wahlbezirkID: string) {
    try {
      const loadedWahlvorschlaegeAsPromises = currentUserWahlMetadata.value.map(
        (metadata) => getWahlvorschlaege(metadata.wahlID, metadata.wahlbezirkID)
      );
      wahlvorschlaege.value = await Promise.all(
        loadedWahlvorschlaegeAsPromises
      );
    } catch {
      throw new Error("Fehler beim resolven der Wahlvorschlaege");
    }
  }

  //function getWahlvorschlaegeByWahlId(wahlId: string) {}

  return {
    wahlvorschlaege,
    loadWahlvorschlaege /*getWahlvorschlaegeByWahlId*/,
  };
});

registerStoreHMR(useKopfdatenStore);
