import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

export function useErgebnisUtils() {
  function orderedByNumIndexWithNullAtEnd(a: Ergebnis, b: Ergebnis) {
    if (a.numIndex === null && b.numIndex === null) {
      return 0;
    }
    if (a.numIndex === null) {
      return 1;
    }
    if (b.numIndex === null) {
      return -1;
    }
    return a.numIndex - b.numIndex;
  }

  function reduceToMaxOfNumIndex(maxValue: number | null, ergebnis: Ergebnis) {
    if (maxValue === null) {
      return ergebnis.numIndex;
    } else if (ergebnis.numIndex === null) {
      return maxValue;
    } else {
      return maxValue < ergebnis.numIndex ? ergebnis.numIndex : maxValue;
    }
  }

  return {
    orderedByNumIndexWithNullAtEnd,
    reduceToMaxOfNumIndex,
  };
}
