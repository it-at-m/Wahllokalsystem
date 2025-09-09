import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";

export interface Wahlscheine {
  bezirkUndWahlID: BezirkUndWahlID;
  stimmabgabevermerke: number;
}
