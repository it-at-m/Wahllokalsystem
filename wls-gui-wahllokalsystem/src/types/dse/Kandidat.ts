import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";

export interface Kandidat {
  kandidatId: string;
  listenposition: number;
  nennung: number;
  name: string;
  durchgestrichen: boolean;
  einzelstimmen: number | null;
  ungueltigeStimmen: number | null;
  reststimmen: number | null;
  owningWahlvorschlag: Wahlvorschlag;
}
