import type { Kandidat } from "@/types/dse/Kandidat.ts";

export interface Wahlvorschlag {
  wahlvorschlagID: string;
  ordnungszahl: number;
  selected: boolean;
  kandidaten: Kandidat[];
}
