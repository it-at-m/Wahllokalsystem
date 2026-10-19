import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed, onActivated, readonly, ref } from "vue";

import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";

const { getWahlvorschlaege } = useWahlvorschlaegeService();

export function useWahlvorschlaegeState(wahlID: string, wahlbezirkID: string) {
  const isWahlvorschlaegeLoading = ref(false);
  const wahlvorschlaege = ref<Wahlvorschlag[]>([]);
  let loadingPromise: Promise<void> | undefined;

  onActivated(async () => {
    await loadWahlvorschlaege();
  });

  function loadWahlvorschlaege(): Promise<void> {
    if (loadingPromise) return loadingPromise;

    isWahlvorschlaegeLoading.value = true;
    loadingPromise = _loadWahlvorschlaege();

    return loadingPromise;
  }

  async function _loadWahlvorschlaege() {
    try {
      wahlvorschlaege.value = (
        await getWahlvorschlaege(wahlID, wahlbezirkID)
      ).wahlvorschlaege;
    } finally {
      isWahlvorschlaegeLoading.value = false;
      loadingPromise = undefined;
    }
  }

  return {
    isWahlvorschlaegeLoading: readonly(isWahlvorschlaegeLoading),
    loadWahlvorschlaege,
    wahlvorschlaege: computed(() => wahlvorschlaege.value),
  };
}
