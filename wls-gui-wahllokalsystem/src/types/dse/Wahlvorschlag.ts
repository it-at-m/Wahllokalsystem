import type { Kandidat } from "@/types/dse/Kandidat.ts";

export interface Wahlvorschlag {
  wahlvorschlagID: string;
  ordnungszahl: number;
  kurzname: string;
  selected: boolean;
  erhaeltStimmen: boolean;
  gueltigeStimmen: number;
  ungueltigeStimmen: number;
  kandidaten: Kandidat[];
}
