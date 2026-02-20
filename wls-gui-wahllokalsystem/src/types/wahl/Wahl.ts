import type { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import type { Stimmzettelumschlaege } from "@/types/ergebnismeldung/common/Stimmzettelumschlaege.ts";
import type { Farbe } from "@/types/wahl/Farbe.ts";

import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

export interface Wahl {
  wahlID: string;
  name: string;
  reihenfolge: number;
  waehlerverzeichnisNummer: number;
  wahltag: string;
  wahlart: WahlWahlartEnum;
  farbe: Farbe | undefined;
  nummer: string | undefined;
  beanstandeteWahlbriefe: (ZurueckweisungsgrundEnum | null)[];
  stimmzettelumschlaege: Stimmzettelumschlaege;
}
