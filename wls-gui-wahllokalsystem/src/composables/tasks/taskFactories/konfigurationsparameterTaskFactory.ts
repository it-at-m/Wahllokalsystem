import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export function useKonfigurationsparameterTaskFactory(): TaskFactory {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  function createTasks(taskFactoryData: TaskFactoryContext): Task[] {
    return [_createTask()];
  }

  function _createTask(): Task {
    const { initKonfigurationsparameter } = useInfomanagementStore();
    return {
      name: "Konfigurationsparameter",
      callback: () => {
        return initKonfigurationsparameter(false);
      },
    };
  }

  return {
    createTasks,
  };
}
