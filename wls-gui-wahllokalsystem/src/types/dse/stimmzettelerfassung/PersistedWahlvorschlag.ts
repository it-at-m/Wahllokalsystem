import type { PersistedKandidat } from "@/types/dse/stimmzettelerfassung/PersistedKandidat.ts";

export interface PersistedWahlvorschlag {
  wahlvorschlagID: string;
  selected: boolean;
  kandidaten: PersistedKandidat[];
}
