import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";

export interface Wahlvorschlag {
  identifikator: string;
  ordnungszahl: number;
  kurzname: string;
  erhaeltStimmen: boolean;
  kandidaten?: Kandidat[];
}
