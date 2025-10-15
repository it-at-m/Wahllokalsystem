import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";

export interface AWerte {
  bezirkUndWahlID: BezirkUndWahlID;
  a1: number;
  a2: number | null;
}
