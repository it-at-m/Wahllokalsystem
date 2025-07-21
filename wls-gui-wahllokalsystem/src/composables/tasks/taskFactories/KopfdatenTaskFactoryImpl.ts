import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export class KopfdatenTaskFactoryImpl implements TaskFactoryInterface {
  createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const taskList: Task[] = [];
    taskFactoryContext.taskFactoryMetaData.forEach((taskFactoryMetaData) => {
      taskList.push(this._createTask(taskFactoryMetaData));
    });
    return taskList;
  }

  _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
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
}
