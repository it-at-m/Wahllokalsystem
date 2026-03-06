import type { StimmzettelKandidatSnapshot } from "@/types/experimental/StimmzettelKandidatSnapshot.ts";

export interface StimmzettelSnapshot {
  selectedWahlvorschlaegeOrdnungszahlen: number[];
  kandidatenSnapshot: StimmzettelKandidatSnapshot[];
}
