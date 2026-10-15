import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed, onActivated, readonly, ref } from "vue";

import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";

const { getWahlvorschlaege } = useWahlvorschlaegeService();

export function useWahlvorschlaegeState(wahlID: string, wahlbezirkID: string) {
  const isWahlvorschlaegeLoading = ref(false);
  const wahlvorschlaege = ref<Wahlvorschlag[]>([]);

  onActivated(async () => {
    await _loadWahlvorschlaege();
  });

  async function _loadWahlvorschlaege() {
    isWahlvorschlaegeLoading.value = true;
    try {
      wahlvorschlaege.value = (
        await getWahlvorschlaege(wahlID, wahlbezirkID)
      ).wahlvorschlaege;
    } finally {
      isWahlvorschlaegeLoading.value = false;
    }
  }

  return {
    isWahlvorschlaegeLoading: readonly(isWahlvorschlaegeLoading),
    wahlvorschlaege: computed(() => wahlvorschlaege.value),
  };
}
