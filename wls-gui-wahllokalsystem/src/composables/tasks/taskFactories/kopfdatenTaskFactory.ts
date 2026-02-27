import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export function useKopfdatenTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(createTask);
  }

  function createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { loadKopfdaten } = useKopfdatenStore();
    return {
      callback: () =>
        loadKopfdaten(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID,
          false
        ),
      name: `Kopfdaten - ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
