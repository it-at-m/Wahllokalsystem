import { defineStore } from "pinia";

import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";

const storeID = "dataSync";

export const useDataSyncStore = defineStore(storeID, () => {
  const { synchronizeOfflineData, isOfflineDataSyncing } = useDataSyncer();

  return {
    isOfflineDataSyncing,
    synchronizeOfflineData,
  };
});
