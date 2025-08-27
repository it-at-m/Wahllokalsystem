import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export interface BezirkUndWahlIDStapelArt extends BezirkUndWahlID {
  stapelArt: StapelArtEnum;
}
