import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";

export interface DseWahlvorschlag {
  wahlvorschlagID: string;
  ordnungszahl: number;
  kurzname: string;
  selected: boolean;
  erhaeltStimmen: boolean;
  gueltigeStimmen: number;
  ungueltigeStimmen: number;
  kandidaten: DseKandidat[];
}
