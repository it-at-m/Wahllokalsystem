import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { ErgebnisWithNumIndex } from "@/types/ergebnisermittlung/ErgebnisWithNumIndex.ts";

export interface ErgebnisWithNumIndexAndStapel extends ErgebnisAndStapelArt {
  ergebnis: ErgebnisWithNumIndex;
}
