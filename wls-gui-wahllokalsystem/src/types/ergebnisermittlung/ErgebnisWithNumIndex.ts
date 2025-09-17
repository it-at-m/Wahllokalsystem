import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

export interface ErgebnisWithNumIndex extends Ergebnis {
  numIndex: number;
}
