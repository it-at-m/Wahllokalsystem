import type { Wahlvorschlag } from "@/types/wahlvorschlag/Wahlvorschlag";

export interface WahlvorschlagListe {
  name: string;
  wahlvorschlaege: Wahlvorschlag[];
}
