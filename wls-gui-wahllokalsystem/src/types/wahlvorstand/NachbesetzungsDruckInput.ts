import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied.ts";

export interface NachbesetzungsDruckInput {
  wahlbezirknummer?: string;
  wahltag?: string;
  wahlvorstaende?: Wahlvorstandsmitglied[];
}
