import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useDSEStimmzettelTaskFactory(): TaskFactory {
  const { getStimmzettel } = useStimmzettelService();
  const { currentUserTeamName } = storeToRefs(useUserStore());

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(
      (metaData: ExtendedWahlMetaData) => _createTask(metaData)
    );
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    return {
      callback: () =>
        getStimmzettel(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID,
          currentUserTeamName.value
        ),
      name: `Stimmzettel für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
