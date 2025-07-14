import type { TaskFactoryMetaData } from "@/composables/tasks/TaskFactoryMetaData.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface TaskFactoryData {
  wahlbezirkArt: WahlbezirksArtEnum;
  taskFactoryMetaData: TaskFactoryMetaData[];
}
