import type { ErgebnisAndStapelArt } from "@/types/ergebnismeldung/common/ErgebnisAndStapelArt.ts";
import type { ErgebnisWithNumIndex } from "@/types/ergebnismeldung/common/ErgebnisWithNumIndex.ts";

export interface ErgebnisWithNumIndexAndStapel extends ErgebnisAndStapelArt {
  ergebnis: ErgebnisWithNumIndex;
}
