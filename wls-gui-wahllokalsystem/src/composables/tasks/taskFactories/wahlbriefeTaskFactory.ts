import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useWahlbriefeTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.wahlbezirkArt == WahlbezirksArtEnum.BWB
      ? [_createTask()]
      : [];
  }

  function _createTask(): Task {
    const { wahlbriefDatenActions } = useWahlbezirkStore();
    return {
      name: "Erfasste Wahlbriefe",
      callback: () => {
        return wahlbriefDatenActions.initWahlbriefdaten(false);
      },
    };
  }

  return {
    createTasks,
  };
}
