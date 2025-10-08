import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useBegruendungTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(_createTask);
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { loadBegruendungForWahl } = useErgebnismeldungStore();
    const { isUWB } = storeToRefs(useUserStore());

    return {
      callback: () => loadBegruendungForWahl(taskFactoryMetaData.wahlID, false),
      name: `Begründung ${isUWB.value ? "Stimmzettel" : "Stimmzettelumschläge"} für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
