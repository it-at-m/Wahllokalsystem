import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { useExperimentalStimmzettelService } from "@/composables/experimental/experimentalStimmzettelService.ts";
import { useStimmzettelDataService } from "@/composables/experimental/stimzettelDataService.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useDSEStimmzettelTaskFactory(): TaskFactory {
  const { currentUserTeamName } = storeToRefs(useUserStore());
  const { registerSyncAdapter } = useDataSyncStore();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(
      (metaData: ExtendedWahlMetaData) => _createTask(metaData)
    );
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    const stimmzettelService = useStimmzettelDataService(
      taskFactoryMetaData.wahlID,
      taskFactoryMetaData.wahlbezirkID
    );

    registerSyncAdapter({ getTasks: stimmzettelService.getTasksToSync });

    return {
      callback: () => {
        if (!stimmzettelService.hasAnyDataLoaded) {
          return stimmzettelService.loadStimmzettelOfTeam(
            currentUserTeamName.value
          );
        } else {
          return Promise.resolve();
        }
      },
      name: `Stimmzettel für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
