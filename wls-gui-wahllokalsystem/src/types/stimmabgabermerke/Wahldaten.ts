import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export interface Wahldaten {
  wahlbezirkID: string;
  wahlID: string;
  waehlerverzeichnisNummer: number;
  eingenommeneWahlscheine: Map<
    EingenommenerWahlscheinStimmzettelartEnum,
    number
  >;
}
