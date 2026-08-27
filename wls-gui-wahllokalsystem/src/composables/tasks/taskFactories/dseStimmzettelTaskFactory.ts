import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { useExperimentalStimmzettelService } from "@/composables/experimental/experimentalStimmzettelService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useDSEStimmzettelTaskFactory(): TaskFactory {
  const { currentUserTeamName } = storeToRefs(useUserStore());

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(
      (metaData: ExtendedWahlMetaData) => _createTask(metaData)
    );
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const { getStimmzettel } = useExperimentalStimmzettelService(
      taskFactoryMetaData.wahlID,
      taskFactoryMetaData.wahlbezirkID
    );
    return {
      callback: () => getStimmzettel(currentUserTeamName.value, false),
      name: `Stimmzettel für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
