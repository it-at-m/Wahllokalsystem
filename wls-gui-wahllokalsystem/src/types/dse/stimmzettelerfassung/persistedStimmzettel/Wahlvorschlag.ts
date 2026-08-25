import type { Kandidat } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Kandidat.ts";

export interface Wahlvorschlag {
  wahlvorschlagID: string;
  selected: boolean;
  kandidaten: Kandidat[];
}
