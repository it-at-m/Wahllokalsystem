import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

export interface Ergebnisse {
  bezirkUndWahlIDStapelart: BezirkUndWahlIDStapelArt;
  ergebnisse: Ergebnis[];
}
