import type { ErgebnisAndKandidat } from "@/types/ergebnismeldung/common/ErgebnisAndKandidat.ts";

export interface WahlvorschlagWithKandidatenErgebnissen {
  identifikator: string;
  ordnungszahl: number;
  kurzname: string;
  kandidatenErgebnisse: ErgebnisAndKandidat[];
}
