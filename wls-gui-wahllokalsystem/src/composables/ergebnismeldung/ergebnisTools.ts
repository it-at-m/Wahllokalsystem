import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

export function useErgebnisTools() {
  function createWithErgebnisOnly(ergebnis: number): Ergebnis {
    return {
      ergebnis: ergebnis,
      wahlvorschlagsOrdnungszahl: null,
      wahlvorschlagID: null,
      numIndex: null,
      kandidatID: null,
    };
  }

  function createWithWahlvorschlagIDAndErgebnis(
    wahlvorschlagID: string,
    ergebnis: number
  ): Ergebnis {
    return {
      ergebnis: ergebnis,
      wahlvorschlagsOrdnungszahl: null,
      wahlvorschlagID: wahlvorschlagID,
      numIndex: null,
      kandidatID: null,
    };
  }

  return {
    createWithErgebnisOnly,
    createWithWahlvorschlagIDAndErgebnis,
  };
}
