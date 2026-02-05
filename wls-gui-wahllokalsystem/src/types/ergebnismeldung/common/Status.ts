import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";
import type { Meldung } from "@/types/ergebnismeldung/common/Meldung.ts";

export interface Status {
  bezirkUndWahlID: BezirkUndWahlID;
  schnellmeldung: Meldung;
  niederschrift: Meldung;
}
