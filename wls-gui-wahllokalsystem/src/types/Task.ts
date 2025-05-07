import type { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

export interface Task {
  wahlbezirksart: string;
  forWahlen: WahlWahlartEnum[] | undefined;
  forAllWVZs: boolean | undefined;
  name: string;
  callback: () => Promise<unknown>;
}
