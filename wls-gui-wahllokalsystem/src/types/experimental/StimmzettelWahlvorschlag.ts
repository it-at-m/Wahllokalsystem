import type { StimmzettelKandidat } from "@/types/experimental/StimmzettelKandidat.ts";

export interface StimmzettelWahlvorschlag {
  identifikator: string;
  ordnungszahl: number;
  kurzname: string;
  erhaeltStimmen: boolean;
  kandidaten: StimmzettelKandidat[];
  isSelected: boolean;
}
