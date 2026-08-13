import type { KandidatAnzeige } from "@/types/dse/KandidatAnzeige.ts";

export interface WahlvorschlagAnzeige {
  identifikator: string;
  ordnungszahl: number;
  kurzname: string;
  erhaeltStimmen: boolean;
  gueltigeStimmen: number;
  ungueltigeStimmen: number;
  kandidaten?: KandidatAnzeige[]; //TODO nicht optional machen
}
