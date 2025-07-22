import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface Task {
  onlyForWahlbezirksart: WahlbezirksArtEnum | undefined;
  onlyForWahlen: WahlWahlartEnum[] | undefined;
  onlyForAllWVaehlerverzeichnisse: boolean | undefined;
  name: string;
  callback: () => Promise<unknown>;
}
