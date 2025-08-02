import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export interface EingenommenerWahlschein {
  anzahl: number;
  stimmzettelart: EingenommenerWahlscheinStimmzettelartEnum;
}
