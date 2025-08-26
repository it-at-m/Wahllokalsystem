import type { BezirkUndWahlIDStapelart } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelart.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

export interface Ergebnisse {
  bezirkUndWahlIDStapelart: BezirkUndWahlIDStapelart;
  ergebnisse: Ergebnis[];
}
