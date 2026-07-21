import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useStimmzettelTaskFactory(): TaskFactory {
  const { loadStimmabgabevermerke } = useStimmabgabevermerkeStore();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(_createTask);
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    return {
      callback: () =>
        loadStimmabgabevermerke(
          taskFactoryMetaData.wahlbezirkID,
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.waehlerverzeichnisNummer,
          false
        ),
      name: `Stimmzettel für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
