import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { computed, onActivated, readonly, ref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";

const { getStimmzettel, saveStimmzettel } = useStimmzettelService();
const { isSamePersistedStimmzettel } = useStimmzettelTools();

export function useStimmzettelState(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const isStimmzettelLoading = ref(false);
  const savedStimmzettel: Ref<PersistedStimmzettel[]> = ref([]);

  const hasStimmzettel = computed(() => savedStimmzettel.value.length > 0);

  onActivated(async () => {
    await _loadStimmzettel();
  });

  async function saveOrUpdateStimmzettel(
    stimmzettelToSave: PersistedStimmzettel
  ) {
    const newStimmzettelCollectionToSave = [...savedStimmzettel.value];
    const stimmzettelExistsIndex = newStimmzettelCollectionToSave.findIndex(
      (savedStimmzettel) =>
        isSamePersistedStimmzettel(savedStimmzettel, stimmzettelToSave)
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
