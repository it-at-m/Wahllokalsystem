import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useAWerteTaskFactory(): TaskFactory {
  const { getAWerte } = useAWerteService();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    if (
      taskFactoryContext.wahlbezirkArt !== WahlbezirksArtEnum.UWB ||
      !taskFactoryContext.isSchriftfuehrung
    ) {
      return [];
    }

    return taskFactoryContext.extendedWahlMetaData.map((metaData) =>
      _createTask(metaData)
    );
  }

  function _createTask(taskMetaData: ExtendedWahlMetaData): Task {
    return {
      name: `AWerte für ${taskMetaData.wahlName}`,
      callback: () => getAWerte(taskMetaData.wahlbezirkID, false),
    };
  }

  return {
    createTasks,
  };
}
