import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { Meldung } from "@/types/ergebnismeldung/Meldung.ts";

export interface Status {
  bezirkUndWahlID: BezirkUndWahlID;
  schnellmeldung: Meldung;
  niederschrift: Meldung;
}
