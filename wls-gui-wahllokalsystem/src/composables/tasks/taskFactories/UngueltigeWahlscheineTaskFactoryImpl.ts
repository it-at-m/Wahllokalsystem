import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export class UngueltigeWahlscheineTaskFactoryImpl
  implements TaskFactoryInterface
{
  createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const taskList: Task[] = [];
    const onlyForWahlbezirksart = WahlbezirksArtEnum.UWB;
    const onlyForWahlen: WahlWahlartEnum[] = [
      WahlWahlartEnum.Btw,
      WahlWahlartEnum.Beb,
      WahlWahlartEnum.Euw,
      WahlWahlartEnum.Obw,
      WahlWahlartEnum.Srw,
      WahlWahlartEnum.Baw,
    ];
    if (
      taskFactoryContext.wahlbezirkArt == onlyForWahlbezirksart &&
      taskFactoryContext.taskFactoryMetaData.some((extendedMetaData) =>
        onlyForWahlen.includes(extendedMetaData.wahlart)
      )
    ) {
      taskList.push(this._createTask());
    }
    return taskList;
  }

  _createTask(): Task {
    const { initUngueltigeWahlscheine } = useWahlbezirkStore();
    return {
      name: "UngültigeWahlscheine",
      callback: () => initUngueltigeWahlscheine(false),
    };
  }
}
