import type { BezirkUndWahlIDModel } from "@/types/ereignismeldung/BezirkUndWahlIDModel.ts";

export interface Wahlscheine {
  bezirkUndWahlID: BezirkUndWahlIDModel;
  stimmabgabevermerke: number;
}
