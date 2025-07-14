import type { TaskFactoryData } from "@/composables/tasks/TaskFactoryData.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { TaskFactoryMetaData } from "@/composables/tasks/TaskFactoryMetaData.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export class KopfdatenTaskFactoryImpl implements TaskFactoryInterface {
  createTasks(taskFactoryData: TaskFactoryData): Task[] {
    const taskList: Task[] = [];
    taskFactoryData.taskFactoryMetaData.forEach((taskFactoryMetaData) => {
      taskList.push(this._createTask(taskFactoryMetaData));
    });
    taskList.filter(
      (task) =>
        task.onlyForWahlbezirksart === taskFactoryData.wahlbezirkArt ||
        task.onlyForWahlbezirksart === undefined
    );
    return taskList;
  }

  _createTask(taskFactoryMetaData: TaskFactoryMetaData): Task {
    const { loadKopfdaten } = useKopfdatenStore();
    return {
      callback: () =>
        loadKopfdaten(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID
        ),
      name: "Kopfdaten - " + taskFactoryMetaData.wahlname,
      onlyForAllWVaehlerverzeichnisse: undefined,
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
    };
  }
}
