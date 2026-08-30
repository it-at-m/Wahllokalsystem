import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { useExperimentalStimmzettelService } from "@/composables/experimental/experimentalStimmzettelService.ts";
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
    const stimmzettelService = useExperimentalStimmzettelService(
      taskFactoryMetaData.wahlID,
      taskFactoryMetaData.wahlbezirkID
    );

    registerSyncAdapter(stimmzettelService);

    return {
      callback: () =>
        stimmzettelService.initOfflineCachedStimmzettel(
          currentUserTeamName.value
        ),
      name: `Stimmzettel für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return {
    createTasks,
  };
}
