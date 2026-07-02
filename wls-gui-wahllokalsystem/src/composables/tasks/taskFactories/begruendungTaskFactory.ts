import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useBegruendungTaskFactory(): TaskFactory {
  const { getStimmzettelTermForWahl } = useTextFormatter();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    if (!taskFactoryContext.isSchriftfuehrung) {
      return [];
    }

    return taskFactoryContext.extendedWahlMetaData.map(_createTask);
  }
  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { wahlenActions } = useWahlenStore();
    const { loadBegruendungForWahl } = useErgebnismeldungStore();

    const wahl = wahlenActions.getWahlOrUndefinedById(
      taskFactoryMetaData.wahlID
    );

    if (!wahl) {
      throw new Error(`Wahl for ID ${taskFactoryMetaData.wahlID} not found`);
    } else {
      return {
        callback: () => loadBegruendungForWahl(wahl, false),
        name: `Begründung ${getStimmzettelTermForWahl(wahl)} für ${taskFactoryMetaData.wahlName}`,
      };
    }
  }

  return {
    createTasks,
  };
}
