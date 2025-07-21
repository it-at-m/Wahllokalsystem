import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export function useKonfigurationsparameterTaskFactory(): TaskFactoryInterface {
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
