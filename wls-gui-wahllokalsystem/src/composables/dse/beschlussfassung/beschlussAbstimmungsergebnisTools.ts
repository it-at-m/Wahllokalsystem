import type { BeschlussAbstimmungsergebnis } from "@/types/dse/beschlussfassung/BeschlussAbstimmungsergebnis.ts";

export function useBeschlussAbstimmungsergebnisTools() {
  function createEmptyAbstimmungsergebnis(): BeschlussAbstimmungsergebnis {
    return {
      stimmenDafuer: null,
      stimmenDagegen: null,
      hasWahlvorsteherVotedDafuer: false,
      abstimmungIsUnentschieden: false,
      abstimmungIsUngueltig: false,
    };
  }

  return { createEmptyAbstimmungsergebnis };
}
