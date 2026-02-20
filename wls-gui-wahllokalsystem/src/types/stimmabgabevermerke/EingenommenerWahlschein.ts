import type { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export interface EingenommenerWahlschein {
  anzahl: number;
  stimmzettelart: EingenommenerWahlscheinStimmzettelartEnum;
}
