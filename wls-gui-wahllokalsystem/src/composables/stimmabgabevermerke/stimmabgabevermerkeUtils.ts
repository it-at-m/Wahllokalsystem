import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

export function useStimmabgabevermerkeUtils() {
  function createEmptyStimmabgabevermerke(
    wahlID: string,
    wahlbezirkID: string,
    waehlerverzeichnisNummer: number
  ): Stimmabgabevermerke {
    return {
      waehlerverzeichnisNummer: waehlerverzeichnisNummer,
      wahlbezirkID: wahlbezirkID,
      wahldaten: [
        {
          wahlbezirkID: wahlbezirkID,
          waehlerverzeichnisNummer: waehlerverzeichnisNummer,
          vermerke: [],
          wahlID: wahlID,
          eingenommeneWahlscheine: new Map(),
        },
      ],
      anzahlBlaetter: 0,
    };
  }

  return {
    createEmptyStimmabgabevermerke,
  };
}
