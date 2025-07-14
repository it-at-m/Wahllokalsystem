import type { TaskFactoryData } from "@/composables/tasks/TaskFactoryData.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export class KonfigurationsparameterTaskFactoryImpl
  implements TaskFactoryInterface
{
  createTasks(taskFactoryData: TaskFactoryData): Task[] {
    const taskList: Task[] = [];
    taskList.push(this._createTask());
    taskList.filter(
      (task) =>
        task.onlyForWahlbezirksart === taskFactoryData.wahlbezirkArt ||
        task.onlyForWahlbezirksart === undefined
    );
    return taskList;
  }

  _createTask(): Task {
    const { initKonfigurationsparameter } = useInfomanagementStore();
    return {
      name: "Konfigurationsparameter",
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => {
        return initKonfigurationsparameter(false);
      },
    };
  }
}
