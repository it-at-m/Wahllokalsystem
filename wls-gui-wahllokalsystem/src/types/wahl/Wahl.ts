import type { Farbe } from "@/types/wahl/Farbe.ts";

import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

export interface Wahl {
  wahlID: string;
  name: string;
  reihenfolge: number;
  waehlerverzeichnisnummer: number;
  wahltag: string;
  wahlart: WahlWahlartEnum;
  farbe: Farbe | undefined;
  nummer: string | undefined;
}
