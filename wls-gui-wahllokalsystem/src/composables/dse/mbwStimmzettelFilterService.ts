import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { computed } from "vue";

export function useMbwStimmzettelFilterService(
  stimmzettel: Ref<PersistedStimmzettel>
) {
  const stapelA = computed(() => stimmzettel.value);
  const stapelB = computed(() => stimmzettel.value);
  const stapelBC = computed(() => stimmzettel.value);
  const stapelDUngueltig = computed(() => stimmzettel.value);
  const stapelEUngueltig = computed(() => stimmzettel.value);

  return {
    stapelA,
    stapelB,
    stapelBC,
    stapelDUngueltig,
    stapelEUngueltig,
  };
}
