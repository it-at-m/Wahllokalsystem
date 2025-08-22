import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";

export function useWahlvorschleageTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(createTask);
  }

  function createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { loadWahlvorschlaege } = useWahlvorschlaegeStore();
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
