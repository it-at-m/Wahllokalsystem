import { defineStore } from "pinia";

import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";

const storeID = "dataSync";

export const useDataSyncStore = defineStore(storeID, () => {
  const {
    synchronizeOfflineData,
    getSyncTasks,
    isOfflineDataSyncing,
    numberOfTasksFinished,
    numberOfTasksToRun,
  } = useDataSyncer();

  return {
    numberOfTasksFinished,
    numberOfTasksToRun,
    isOfflineDataSyncing,
    synchronizeOfflineData,
    getSyncTasks,
  };
});
