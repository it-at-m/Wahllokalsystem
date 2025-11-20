import type { ErgebnisAndKandidat } from "@/types/ergebnisermittlung/ErgebnisAndKandidat.ts";

export interface WahlvorschlagWithKandidatenErgebnissen {
  identifikator: string;
  ordnungszahl: number;
  kurzname: string;
  kandidatenErgebnisse: ErgebnisAndKandidat[];
}
