import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface TaskFactoryContext {
  wahlbezirkArt: WahlbezirksArtEnum;
  isSchriftfuehrung: boolean;
  isErfassungsteam: boolean;
  extendedWahlMetaData: ExtendedWahlMetaData[];
}
