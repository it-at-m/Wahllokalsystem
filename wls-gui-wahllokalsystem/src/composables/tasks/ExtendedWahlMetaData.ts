import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export interface ExtendedWahlMetaData extends WahlMetaData {
  wahlName: string;
  wahlArt: WahlWahlartEnum;
  waehlerverzeichnisNummer: number;
}
