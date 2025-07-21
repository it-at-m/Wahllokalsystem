import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useUngueltigeWahlscheineTaskFactory(): TaskFactoryInterface {
  const onlyForWahlen: WahlWahlartEnum[] = [
    WahlWahlartEnum.Btw,
    WahlWahlartEnum.Beb,
    WahlWahlartEnum.Euw,
    WahlWahlartEnum.Obw,
    WahlWahlartEnum.Srw,
    WahlWahlartEnum.Baw,
  ];

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const taskList: Task[] = [];
    if (
      taskFactoryContext.wahlbezirkArt == WahlbezirksArtEnum.UWB &&
      taskFactoryContext.extendedWahlMetaData.some((extendedMetaData) =>
        onlyForWahlen.includes(extendedMetaData.wahlart)
      )
    ) {
      taskList.push(_createTask());
    }
    return taskList;
  }

  function _createTask(): Task {
    const { initUngueltigeWahlscheine } = useWahlbezirkStore();
    return {
      name: "UngültigeWahlscheine",
      callback: () => initUngueltigeWahlscheine(false),
    };
  }

  return {
    createTasks,
  };
}
