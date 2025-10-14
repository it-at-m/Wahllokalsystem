import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";

export interface AWerte {
  bezirkUndWahlId: BezirkUndWahlID;
  a1: number;
  a2: number | null;
}
