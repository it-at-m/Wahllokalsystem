import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";
import type { Meldung } from "@/types/ergebnismeldung/common/Meldung.ts";

export interface Status {
  /*
TODO: neuer Name? ElectionStatus weil es noch einen Status ohne eine bestimmte Wahl gibt?
*/
  bezirkUndWahlID: BezirkUndWahlID;
  schnellmeldung: Meldung;
  niederschrift: Meldung;
  stepsDone: Record<string, boolean>;
}
