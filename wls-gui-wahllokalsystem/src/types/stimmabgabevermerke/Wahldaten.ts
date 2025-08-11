import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export interface Wahldaten {
  wahlID: string;
  waehlerverzeichnisNummer: number;
  eingenommeneWahlscheine: Map<
    EingenommenerWahlscheinStimmzettelartEnum,
    number
  >;
}
