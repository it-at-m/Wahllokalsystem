import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

export interface Stimmabgabevermerke {
  waehlerverzeichnisNummer: number;
  anzahlBlaetter: number;
  wahldaten: Wahldaten[];
  wahlbezirkID: string;
}
