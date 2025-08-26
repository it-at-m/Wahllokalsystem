import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export interface BezirkUndWahlIDStapelart extends BezirkUndWahlID {
  stapelArt: StapelArtEnum;
}
