import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export function useKopfdatenTaskFactory(): TaskFactoryInterface {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const taskList: Task[] = [];
    taskFactoryContext.extendedWahlMetaData.forEach((taskFactoryMetaData) => {
      taskList.push(createTask(taskFactoryMetaData));
    });
    return taskList;
  }

  function createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { loadKopfdaten } = useKopfdatenStore();
    return {
      callback: () =>
        loadKopfdaten(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID
        ),
      name: "Kopfdaten - " + taskFactoryMetaData.wahlname,
    };
  }

  return {
    createTasks,
  };
}
