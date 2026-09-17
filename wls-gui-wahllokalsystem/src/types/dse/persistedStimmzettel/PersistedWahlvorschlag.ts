import type { Kandidat } from "@/types/dse/persistedStimmzettel/Kandidat.ts";

export interface PersistedWahlvorschlag {
  wahlvorschlagID: string;
  selected: boolean;
  kandidaten: Kandidat[];
}
