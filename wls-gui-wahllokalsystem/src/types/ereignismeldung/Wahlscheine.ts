import type { BezirkUndWahlID } from "@/types/ereignismeldung/BezirkUndWahlID.ts";

export interface Wahlscheine {
  bezirkUndWahlID: BezirkUndWahlID;
  stimmabgabevermerke: number;
}
