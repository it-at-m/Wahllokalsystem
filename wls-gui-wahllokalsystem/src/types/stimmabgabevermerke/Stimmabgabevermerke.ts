import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

export interface Stimmabgabevermerke {
  wahlID: string;
  waehlerverzeichnisNummer: number;
  wahlbezirkID: string;
  eingenommeneWahlscheine: Map<
    EingenommenerWahlscheinStimmzettelartEnum,
    number
  >;
  vermerke: Vermerke[];
}
