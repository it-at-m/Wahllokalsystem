import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { usePersistedStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/PersistedStimmzettelTools.ts";

export function useMbwStimmzettelFilterService(
  stimmzettel: Ref<PersistedStimmzettel[]>
) {
  const {
    matchesMBWStapelA,
    matchesMBWStapelB,
    matchesMBWStapelBC,
    matchesMBWStapelDUngueltig,
    matchesMBWStapelEUngueltig,
  } = usePersistedStimmzettelTools();

  const stapelA = computed(() => stimmzettel.value.filter(matchesMBWStapelA));
  const stapelB = computed(() => stimmzettel.value.filter(matchesMBWStapelB));
  const stapelBC = computed(() => stimmzettel.value.filter(matchesMBWStapelBC));
  const stapelDUngueltig = computed(() =>
    stimmzettel.value.filter(matchesMBWStapelDUngueltig)
  );
  const stapelEUngueltig = computed(() =>
    stimmzettel.value.filter(matchesMBWStapelEUngueltig)
  );

  return {
    stapelA,
    stapelB,
    stapelBC,
    stapelDUngueltig,
    stapelEUngueltig,
  };
}
