import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export interface ErgebnisAndStapelArt {
  ergebnis: Ergebnis;
  stapelArt: StapelArtEnum;
}
