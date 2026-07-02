import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useWahlscheineTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.wahlbezirkArt == WahlbezirksArtEnum.BWB &&
      taskFactoryContext.isSchriftfuehrung
      ? taskFactoryContext.extendedWahlMetaData.map(createTask)
      : [];
  }

  function createTask(extendedWahlMetaData: ExtendedWahlMetaData): Task {
    const { loadWahlscheine } = useWahlscheineStore();
    return {
      name: `Wahlscheine - ${extendedWahlMetaData.wahlName}`,
      callback: () => {
        return loadWahlscheine(
          extendedWahlMetaData.wahlID,
          extendedWahlMetaData.wahlbezirkID,
          false
        );
      },
    };
  }

  return {
    createTasks,
  };
}
