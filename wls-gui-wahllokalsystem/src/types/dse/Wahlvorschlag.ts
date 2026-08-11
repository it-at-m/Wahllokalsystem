import type { Kandidat } from "@/types/dse/Kandidat.ts";

export interface Wahlvorschlag {
  wahlvorschlagID: string;
  selected: boolean;
  kandidaten: Kandidat[];
}
