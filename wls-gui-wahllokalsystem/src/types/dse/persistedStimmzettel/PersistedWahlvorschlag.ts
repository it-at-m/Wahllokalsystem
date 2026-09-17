import type { PersistedKandidat } from "@/types/dse/persistedStimmzettel/PersistedKandidat.ts";

export interface PersistedWahlvorschlag {
  wahlvorschlagID: string;
  selected: boolean;
  kandidaten: PersistedKandidat[];
}
