import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";

export interface ElectionWorkflowState {
  bezirkUndWahlID: BezirkUndWahlID;
  isSchnellmeldungDone: boolean;
  isNiederschriftDone: boolean;
  stepsDone: Record<string, boolean>;
}
