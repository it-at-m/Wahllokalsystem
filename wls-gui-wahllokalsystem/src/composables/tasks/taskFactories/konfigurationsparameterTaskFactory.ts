import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export function useKonfigurationsparameterTaskFactory(): TaskFactory {
  function createTasks(): Task[] {
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
