import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

export interface Stimmabgabevermerke {
  wahlbezirkID: string;
  waehlerverzeichnisNummer: number;
  anzahlBlaetter: number;
  wahldaten: Set<Wahldaten>;
}
