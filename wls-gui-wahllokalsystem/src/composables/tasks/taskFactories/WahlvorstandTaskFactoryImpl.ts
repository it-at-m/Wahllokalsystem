import type { TaskFactoryData } from "@/composables/tasks/TaskFactoryData.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

export class WahlvorstandTaskFactoryImpl implements TaskFactoryInterface {
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
    const { initWahlvorstand } = useWahlvorstandStore();
    return {
      name: "Wahlvorstand",
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => {
        return initWahlvorstand(false);
      },
    };
  }
}
