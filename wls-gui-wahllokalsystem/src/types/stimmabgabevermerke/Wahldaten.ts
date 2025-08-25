import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

export interface Wahldaten {
  wahlID: string;
  waehlerverzeichnisNummer: number;
  wahlbezirkID: string;
  eingenommeneWahlscheine: Map<
    EingenommenerWahlscheinStimmzettelartEnum,
    number
  >;
  vermerke: Vermerke[];
}
