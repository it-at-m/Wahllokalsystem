import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";

export interface ElectionWorkflow {
  bezirkUndWahlID: BezirkUndWahlID;
  isSchnellmeldungDone: boolean;
  isNiederschriftDone: boolean;
  stepsDone: Record<string, boolean>;
}
