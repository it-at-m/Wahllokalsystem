import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";

export interface Wahltag {
  wahltag: Date;
  events: WahltagEvent[];
  /**
   * Kennzeichnet, ob es sich um den aktuell aktiven (konfigurierten) Wahltag
   * handelt. Wird von der einbindenden View befüllt.
   */
  active?: boolean;
}
