import type { Kandidat } from "@/types/dse/Kandidat.ts";

export interface Stimmzettel {
  stimmzettelkennung: number;
  selectedWahlvorschlaegeOrdnungszahlen: number[];
  kandidaten: Kandidat[];
}
