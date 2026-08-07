import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";

export interface KandidatAnzeige {
  kandidat: Kandidat;
  nennungsposition: number;
  durchgestrichen: boolean;
  gesamtStimmen: number;
  gueltigeStimmen: number;
  ungueltigeStimmen: number;
  restStimmen: number;
}
