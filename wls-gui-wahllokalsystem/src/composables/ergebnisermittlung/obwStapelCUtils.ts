import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

export function useOBWStapelCUtils(
  wahlvorschlaege: ComputedRef<Wahlvorschlag[]>,
  ergebnisseStapelCUngueltig: ComputedRef<Ergebnis[]>,
  ergebnisseStapelCGueltig: ComputedRef<Ergebnis[]>
) {
  const stapelCUngueltigErgebnisseSum = computed(() =>
    ergebnisseStapelCUngueltig.value.reduce(
      (sum, ergebnis) => sum + (ergebnis.ergebnis ?? 0),
      0
    )
  );

  const stapelCGueltigErgebnisseSums = computed(() =>
    ergebnisseStapelCGueltig.value.reduce(
      (sumOfWahlvorschlag: Map<string, number>, ergebnis) => {
        if (ergebnis.wahlvorschlagID !== null && ergebnis.ergebnis !== null) {
          const currentSum =
            sumOfWahlvorschlag.get(ergebnis.wahlvorschlagID) || 0;
          sumOfWahlvorschlag.set(
            ergebnis.wahlvorschlagID,
            currentSum + ergebnis.ergebnis
          );
        }
        return sumOfWahlvorschlag;
      },
      new Map<string, number>()
    )
  );

  const wahlvorschlaegeAndSumAboveZero = computed(() =>
    wahlvorschlaege.value
      .filter((wahlvorschlag) =>
        stapelCGueltigErgebnisseSums.value.has(wahlvorschlag.identifikator)
      )
      .map((wahlvorschlag) => ({
        wahlvorschlag,
        sum:
          stapelCGueltigErgebnisseSums.value.get(wahlvorschlag.identifikator) ||
          0,
      }))
  );

  const totalSum = computed(
    () =>
      stapelCUngueltigErgebnisseSum.value +
      [...stapelCGueltigErgebnisseSums.value.values()].reduce(
        (sum, currentValue) => sum + currentValue,
        0
      )
  );
  return {
    stapelCUngueltigErgebnisseSum,
    stapelCGueltigErgebnisseSums,
    wahlvorschlaegeAndSumAboveZero,
    totalSum,
  };
}
