import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";

export interface ErgebnisAndKandidat {
  ergebnis: Ergebnis;
  kandidat: Kandidat;
}
