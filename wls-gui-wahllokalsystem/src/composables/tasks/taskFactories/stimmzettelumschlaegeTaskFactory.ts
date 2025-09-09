import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useStimmzettelumschlaegeTaskFactory(): TaskFactory {
  const { stimmzettelumschlaegeActions } = useWahlenStore();
  const { isUWB } = storeToRefs(useUserStore());

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(_createTask);
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const taskName = isUWB.value
      ? "Stimmzettel für "
      : "Stimmzettelumschläge für ";
    return {
      callback: () =>
        stimmzettelumschlaegeActions.loadStimmzettelumschlaege(
          taskFactoryMetaData.wahlID,
          false
        ),
      name: taskName + taskFactoryMetaData.wahlName,
    };
  }

  return {
    createTasks,
  };
}
