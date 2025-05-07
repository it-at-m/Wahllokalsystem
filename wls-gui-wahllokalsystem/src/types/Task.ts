import type { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface Task {
  wahlbezirksart: WahlbezirksArtEnum | undefined;
  forWahlen: WahlWahlartEnum[] | undefined;
  forAllWVZs: boolean | undefined;
  name: string;
  callback: () => Promise<unknown>;
}
