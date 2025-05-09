import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface Task {
  wahlbezirksart: WahlbezirksArtEnum | undefined;
  onlyForWahlen: WahlWahlartEnum[] | undefined;
  onlyForAllWVZs: boolean | undefined; // wahlerverzeichnisse
  name: string;
  callback: () => Promise<unknown>;
}
