import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

export interface DseKandidat {
  kandidatId: string;
  listenposition: number;
  ordnungszahl: number;
  nennung: number;
  name: string;
  durchgestrichen: boolean;
  einzelstimmen: number | null;
  ungueltigeStimmen: number | null;
  reststimmen: number | null;
  owningWahlvorschlag: Wahlvorschlag;
}
