import type { Kandidat } from "@/types/dse/persistedStimmzettel/Kandidat.ts";

export interface Wahlvorschlag {
  wahlvorschlagID: string;
  selected: boolean;
  kandidaten: Kandidat[];
}
