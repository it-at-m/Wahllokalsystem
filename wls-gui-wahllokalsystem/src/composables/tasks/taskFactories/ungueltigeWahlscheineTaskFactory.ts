import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useUngueltigeWahlscheineTaskFactory(): TaskFactory {
  const onlyForWahlen: WahlWahlartEnum[] = [
    WahlWahlartEnum.Btw,
    WahlWahlartEnum.Beb,
    WahlWahlartEnum.Euw,
    WahlWahlartEnum.Obw,
    WahlWahlartEnum.Srw,
    WahlWahlartEnum.Baw,
  ];

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const isUwb = taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.UWB;
    const hasRelevantWahlart = taskFactoryContext.extendedWahlMetaData.some(
      (extendedMetaData) => onlyForWahlen.includes(extendedMetaData.wahlArt)
    );
    return isUwb && hasRelevantWahlart ? [_createTask()] : [];
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
