import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useUngueltigeWahlscheineTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const isUwb = taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.UWB;
    return isUwb ? [_createTask()] : [];
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
