import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useBeanstandeteWahlbriefeTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.BWB
      ? [_createTask()]
      : [];
  }

  function _createTask(): Task {
    const { beanstandeteWahlbriefeActions } = useWahlenStore();
    return {
      callback: () =>
        beanstandeteWahlbriefeActions.initBeanstandeteWahlbriefe(false),
      name: `Zugelassene Wahlbriefe`,
    };
  }

  return { createTasks };
}
