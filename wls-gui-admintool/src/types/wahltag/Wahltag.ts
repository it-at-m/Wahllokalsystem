import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";

export interface Wahltag {
  wahltag: Date;
  events: WahltagEvent[];
}
