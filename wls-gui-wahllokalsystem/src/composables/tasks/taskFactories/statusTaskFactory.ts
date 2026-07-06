import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useStatusStore } from "@/stores/statusStore.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useStatusTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(_createTask);
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { loadStatus } = useStatusStore();
    return {
      name: `Druckstatus - ${taskFactoryMetaData.wahlName}`,
      callback: () =>
        loadStatus(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID,
          false
        ),
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
