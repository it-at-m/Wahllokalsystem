import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useStimmabgabevermerkeTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    if (taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.UWB) {
      return taskFactoryContext.extendedWahlMetaData.map(createTask);
    }
    return [];
  }

  function createTask(extendedWahlMetaData: ExtendedWahlMetaData): Task {
    const { loadStimmabgabevermerke } = useStimmabgabevermerkeStore();
    return {
      callback: () =>
        loadStimmabgabevermerke(
          extendedWahlMetaData.wahlbezirkID,
          extendedWahlMetaData.wahlID,
          extendedWahlMetaData.waehlerverzeichnisNummer,
          false
        ),
      name: `Stimmabgabevermerke-${extendedWahlMetaData.wahlArt}-WVZ-${extendedWahlMetaData.waehlerverzeichnisNummer}-${extendedWahlMetaData.wahlnummer}`,
    };
  }

  return {
    createTasks,
  };
}
