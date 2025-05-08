import type { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface Task {
  wahlbezirksart: WahlbezirksArtEnum | undefined;
  onlyForWahlen: WahlWahlartEnum[] | undefined;
  onlyForAllWVZs: boolean | undefined;
  name: string;
  callback: () => Promise<unknown>;
}
