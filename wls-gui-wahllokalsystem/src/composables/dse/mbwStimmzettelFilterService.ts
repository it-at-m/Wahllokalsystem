import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";
import { usePersistedStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/PersistedStimmzettelTools.ts";

export function useMbwStimmzettelFilterService(
  stimmzettel: Ref<PersistedStimmzettel[]>
) {
  const {
    matchesMBWStapelA,
    matchesMBWStapelB,
    matchesMBWStapelC,
    matchesMBWStapelDUngueltig,
    matchesMBWStapelEUngueltig,
  } = usePersistedStimmzettelTools();

  const stapelA = computed(() => stimmzettel.value.filter(matchesMBWStapelA));
  const stapelB = computed(() => stimmzettel.value.filter(matchesMBWStapelB));
  const stapelC = computed(() => stimmzettel.value.filter(matchesMBWStapelC));
  const stapelDUngueltig = computed(() =>
    stimmzettel.value.filter(matchesMBWStapelDUngueltig)
  );
  const stapelEUngueltig = computed(() =>
    stimmzettel.value.filter(matchesMBWStapelEUngueltig)
  );

  const stapelASumGroupedByWahlvorschlag = computed(() => {
    const sumTool = useStringNumberMapTools(new Map<string, number>());
    stapelA.value
      .flatMap((stimmzettel) => stimmzettel.wahlvorschlaege)
      .forEach((wahlvorschlag) =>
        sumTool.add(wahlvorschlag.wahlvorschlagID, 1)
      );
    return sumTool;
  });
  const stapelBSumGroupedByWahlvorschlag = computed(() => {
    const sumTool = useStringNumberMapTools(new Map<string, number>());
    stapelB.value
      .flatMap((stimmzettel) => stimmzettel.wahlvorschlaege)
      .forEach((wahlvorschlag) =>
        sumTool.add(wahlvorschlag.wahlvorschlagID, 1)
      );
    return sumTool;
  });

  return {
    stapelA,
    stapelASumGroupedByWahlvorschlag,
    stapelB,
    stapelBSumGroupedByWahlvorschlag,
    stapelC,
    stapelDUngueltig,
    stapelEUngueltig,
  };
}
