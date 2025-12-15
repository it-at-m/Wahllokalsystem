import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export interface MbwErgebnisseAndWahlvorschlag {
  ergebnisStapelA: Ergebnis;
  ergebnisStapelB: Ergebnis;
  wahlvorschlag: Wahlvorschlag;
}
