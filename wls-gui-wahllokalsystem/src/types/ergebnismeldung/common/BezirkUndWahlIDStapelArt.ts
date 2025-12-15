import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export interface BezirkUndWahlIDStapelArt extends BezirkUndWahlID {
  stapelArt: StapelArtEnum;
}
