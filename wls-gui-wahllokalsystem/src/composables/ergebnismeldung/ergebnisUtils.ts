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

  return {
    orderedByNumIndexWithNullAtEnd,
  };
}
