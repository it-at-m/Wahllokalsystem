import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useStimmzettelumschlaegeTaskFactory(): TaskFactory {
  const { stimmzettelumschlaegeActions, wahlenActions } = useWahlenStore();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(_createTask);
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { getStimmzettelTermForWahl } = useTextFormatter();

    const wahl = wahlenActions.getWahlOrUndefinedById(
      taskFactoryMetaData.wahlID
    );

    if (!wahl) {
      throw new Error(`Wahl for ID ${taskFactoryMetaData.wahlID} not found`);
    } else {
      return {
        callback: () =>
          stimmzettelumschlaegeActions.loadStimmzettelumschlaege(
            taskFactoryMetaData.wahlID,
            false
          ),
        name:
          getStimmzettelTermForWahl(wahl) + " " + taskFactoryMetaData.wahlName,
      };
    }
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
