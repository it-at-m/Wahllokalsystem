import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export interface ErgebnisAndStapelArt {
  ergebnis: Ergebnis;
  stapelArt: StapelArtEnum;
}
