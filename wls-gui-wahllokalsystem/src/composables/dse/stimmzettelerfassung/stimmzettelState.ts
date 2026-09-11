import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Ref } from "vue";

import { computed, onActivated, readonly, ref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";

const { getStimmzettel, saveStimmzettel } = useStimmzettelService();

export function useStimmzettelState(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const isStimmzettelLoading = ref(false);
  const savedStimmzettel: Ref<Stimmzettel[]> = ref([]);

  const hasStimmzettel = computed(() => savedStimmzettel.value.length > 0);

  onActivated(async () => {
    await _loadStimmzettel();
  });

  async function saveOrUpdateStimmzettel(stimmzettelToSave: Stimmzettel) {
    const newStimmzettelCollectionToSave = [...savedStimmzettel.value];
    const stimmzettelExistsIndex = newStimmzettelCollectionToSave.findIndex(
      (savedStimmzettel) =>
        savedStimmzettel.stimmzettelkennung ===
        stimmzettelToSave.stimmzettelkennung
    );

    if (stimmzettelExistsIndex !== -1) {
      newStimmzettelCollectionToSave[stimmzettelExistsIndex] =
        stimmzettelToSave;
    } else {
      newStimmzettelCollectionToSave.push(stimmzettelToSave);
    }

    await saveStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      newStimmzettelCollectionToSave
    );
    savedStimmzettel.value = newStimmzettelCollectionToSave;
  }

  async function _loadStimmzettel() {
    isStimmzettelLoading.value = true;
    try {
      savedStimmzettel.value = await getStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID
      );
    } finally {
      isStimmzettelLoading.value = false;
    }
  }

  return {
    isStimmzettelLoading: readonly(isStimmzettelLoading),
    savedStimmzettel: computed(() => savedStimmzettel.value),
    hasStimmzettel,
    saveOrUpdateStimmzettel,
  };
}
