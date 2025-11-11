import type { ErgebnisAndKandidat } from "@/types/ergebnisermittlung/ErgebnisAndKandidat.ts";

export interface WahlvorschlagWithScorableKandidaten {
  identifikator: string;
  ordnungszahl: number;
  kurzname: string;
  scorableKandidaten: ErgebnisAndKandidat[];
}
