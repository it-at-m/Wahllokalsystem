import type { Wahldaten } from "@/types/stimmabgabermerke/Wahldaten.ts";

export interface Stimmabgabevermerke {
  wahlbezirkID: string;
  waehlerverzeichnisNummer: number;
  anzahlBlaetter: number;
  wahldaten: Set<Wahldaten>;
}
