import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export interface Wahldaten {
  wahlbezirkID: string;
  wahlID: string;
  waehlerverzeichnisNummer: number;
  eingenommeneWahlscheine: Map<
    EingenommenerWahlscheinStimmzettelartEnum,
    number
  >;
}
