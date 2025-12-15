import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/common/BezirkUndWahlIDStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

export interface Ergebnisse {
  bezirkUndWahlIDStapelart: BezirkUndWahlIDStapelArt;
  ergebnisse: Ergebnis[];
}
