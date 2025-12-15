import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

export interface ErgebnisWithNumIndex extends Ergebnis {
  numIndex: number;
}
