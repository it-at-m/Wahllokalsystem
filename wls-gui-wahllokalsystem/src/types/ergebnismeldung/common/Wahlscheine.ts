import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";

export interface Wahlscheine {
  bezirkUndWahlID: BezirkUndWahlID;
  stimmabgabevermerke: number | null;
}
