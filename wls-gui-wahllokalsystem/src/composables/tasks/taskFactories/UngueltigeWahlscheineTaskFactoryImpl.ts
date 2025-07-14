import type { TaskFactoryData } from "@/composables/tasks/TaskFactoryData.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export class UngueltigeWahlscheineTaskFactoryImpl
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
    const { initUngueltigeWahlscheine } = useWahlbezirkStore();
    return {
      name: "UngültigeWahlscheine",
      onlyForWahlbezirksart: WahlbezirksArtEnum.UWB,
      onlyForWahlen: [
        WahlWahlartEnum.Btw,
        WahlWahlartEnum.Beb,
        WahlWahlartEnum.Euw,
        WahlWahlartEnum.Obw,
        WahlWahlartEnum.Srw,
        WahlWahlartEnum.Baw,
      ],
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => initUngueltigeWahlscheine(false),
    };
  }
}
