import type { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

export interface Stimmzettel {
  anzahl: number | null;
  stimmzettelart: StimmzettelStimmzettelartEnum;
}
