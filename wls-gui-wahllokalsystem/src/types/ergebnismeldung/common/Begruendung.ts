import type { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export interface Begruendung {
  wahlID: string;
  stapelart: StapelArtEnum;
  grund?: string;
  nachzaehlung?: boolean;
  unstimmigkeiten?: boolean;
}
