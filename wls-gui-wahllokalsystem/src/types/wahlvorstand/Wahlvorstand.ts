import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied";

export interface Wahlvorstand {
  wahlvorstandsmitglieder: Wahlvorstandsmitglied[];
}

export function createEmptyWahlvorstand(): Wahlvorstand {
  return {
    wahlvorstandsmitglieder: [],
  };
}
