import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

export function useWahlvorschlaegeTaskFactory(): TaskFactory {
  const { loadWahlvorschlaege } = useWahlvorschlaegeStore();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const allWahlenWithoutBEandVE =
      taskFactoryContext.extendedWahlMetaData.filter(
        (obj) =>
          obj.wahlArt !== WahlWahlartEnum.Ve &&
          obj.wahlArt !== WahlWahlartEnum.Beb
      );

    return allWahlenWithoutBEandVE.map(createTask);
  }

  function createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    return {
      callback: () =>
        loadWahlvorschlaege(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID
        ),
      name: `Wahlvorschläge - ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
