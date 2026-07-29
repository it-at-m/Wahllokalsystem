import { defineStore } from "pinia";

import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";

const storeID = "dataSync";

export const useDataSyncStore = defineStore(storeID, () => {
  const {
    synchronizeOfflineData,
    getSyncTasks,
    dirtyTasksAfterSync,
    hasTasksToRun,
    hasDirtyTasksAfterSync,
    isOfflineDataSyncing,
    numberOfDirtyTasksAfterSync,
    numberOfTasksFinished,
    numberOfTasksToRun,
    lastSyncUpdateTime,
  } = useDataSyncer();

  return {
    dirtyTasksAfterSync,
    hasDirtyTasksAfterSync,
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
