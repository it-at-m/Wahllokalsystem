import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied.ts";

export interface NachbesetzungsDruckInput {
  wahlbezirknummer: string;
  wahlvorstaende: Wahlvorstandsmitglied[];
  druckZeitpunkt: string;
  wahlName: string;
  wahlTag: string;
}
