import type { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useMbwErgebnisseAndWahlvorschlagTools } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagTools.ts";

export function useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
  wahlvorschlaege: ComputedRef<Wahlvorschlag[]>,
  sumtoolStapelA: ComputedRef<ReturnType<typeof useStringNumberMapTools>>,
  sumtoolStapelB: ComputedRef<ReturnType<typeof useStringNumberMapTools>>
) {
  const { createWithErgebnissen } = useMbwErgebnisseAndWahlvorschlagTools();
  const wahlvorschlaegeErgebnisseStapelAAndB = computed<
    MbwErgebnisseAndWahlvorschlag[]
  >(() => {
    return wahlvorschlaege.value.map((wahlvorschlag) => {
      return createWithErgebnissen(
        wahlvorschlag,
        sumtoolStapelA.value.getOrDefault(wahlvorschlag.identifikator),
        sumtoolStapelB.value.getOrDefault(wahlvorschlag.identifikator)
      );
    });
  });

  return {
    wahlvorschlaegeErgebnisseStapelAAndB,
  };
}
