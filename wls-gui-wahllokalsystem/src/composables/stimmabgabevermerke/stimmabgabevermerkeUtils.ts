import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

export function useStimmabgabevermerkeUtils() {
  function createEmptyStimmabgabevermerke(
    wahlID: string,
    wahlbezirkID: string,
    waehlerverzeichnisNummer: number
  ): Stimmabgabevermerke {
    return {
      wahlbezirkID,
      waehlerverzeichnisNummer,
      vermerke: [],
      wahlID,
      eingenommeneWahlscheine: new Map(),
    };
  }

  return {
    createEmptyStimmabgabevermerke,
  };
}
