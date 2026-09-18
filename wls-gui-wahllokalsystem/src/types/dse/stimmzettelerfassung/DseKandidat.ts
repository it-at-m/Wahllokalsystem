import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";

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
  owningWahlvorschlag: DseWahlvorschlag;
}
