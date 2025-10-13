import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export interface Begruendung {
  wahlID: string;
  stapelart: StapelArtEnum;
  grund?: string;
  nachzaehlung?: boolean;
  unstimmigkeiten?: boolean;
}
