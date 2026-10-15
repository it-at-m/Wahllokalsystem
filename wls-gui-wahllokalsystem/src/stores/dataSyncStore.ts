import { defineStore } from "pinia";

import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";

const storeID = "dataSync";

export const useDataSyncStore = defineStore(storeID, () => {
  const {
    synchronizeOfflineData,
    getSyncTasks,
    dirtyTasksAfterSync,
    hasTasksToRun,
    isOfflineDataSyncing,
    numberOfDirtyTasksAfterSync,
    numberOfTasksFinished,
    numberOfTasksToRun,
    lastSyncUpdateTime,
  } = useDataSyncer();

  return {
    dirtyTasksAfterSync,
    hasTasksToRun,
    numberOfDirtyTasksAfterSync,
    numberOfTasksFinished,
    numberOfTasksToRun,
    isOfflineDataSyncing,
    lastSyncUpdateTime,
    synchronizeOfflineData,
    getSyncTasks,
  };
});
