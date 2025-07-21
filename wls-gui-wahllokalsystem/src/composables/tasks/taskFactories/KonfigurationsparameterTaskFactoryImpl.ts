import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export class KonfigurationsparameterTaskFactoryImpl
  implements TaskFactoryInterface
{
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  createTasks(taskFactoryData: TaskFactoryContext): Task[] {
    const taskList: Task[] = [];
    taskList.push(this._createTask());
    return taskList;
  }

  _createTask(): Task {
    const { initKonfigurationsparameter } = useInfomanagementStore();
    return {
      name: "Konfigurationsparameter",
      callback: () => {
        return initKonfigurationsparameter(false);
      },
    };
  }
}
