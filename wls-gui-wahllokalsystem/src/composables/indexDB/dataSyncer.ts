import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import axios from "axios";
import { ref } from "vue";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useIndexDBUtils } from "@/composables/indexDB/indexDBUtils.ts";
import { useTaskManager } from "@/composables/tasks/taskManager.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const indexDBSingleton = useIndexDB();
const { compareByTimestamp } = useIndexDBUtils();
const { logDebug } = useLogging("dataSyncer");

export function useDataSyncer() {
  const taskManager = useTaskManager();
  const isOfflineDataSyncing = ref(false);
  const lastSyncUpdateTime = ref<null | Date>(null);

  async function getSyncTasks() {
    const itemsToSync = await indexDBSingleton.getDirtyItems();
    itemsToSync.sort(_compareSyncItemByTimeStamp);
    return itemsToSync.map((item) => ({
      name: item.key,
      callback: () =>
        axios.request(
          basicPostConfig(
            item.key,
            FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
            _parseDataBasedOnContentType(item.item.data)
          )
        ),
    }));
  }

  async function synchronizeOfflineData() {
    isOfflineDataSyncing.value = true;
    try {
      taskManager.setTasks(await getSyncTasks());
      await taskManager.runAllTasks();
    } catch (e) {
      logDebug("Fehler beim Synchronisieren", e);
    } finally {
      isOfflineDataSyncing.value = false;
      lastSyncUpdateTime.value = new Date();
    }
  }

  function _compareSyncItemByTimeStamp(
    a: { item: IndexDBValue },
    b: { item: IndexDBValue }
  ) {
    return compareByTimestamp(a.item, b.item);
  }

  function _parseDataBasedOnContentType(
    data: string | ArrayBuffer | null
  ): object | undefined {
    if (data && typeof data === "string") {
      return JSON.parse(data);
    } else if (data && data instanceof ArrayBuffer) {
      return data;
    } else {
      return undefined;
    }
  }

  return {
    ...taskManager,
    getSyncTasks,
    synchronizeOfflineData,
    lastSyncUpdateTime,
    isOfflineDataSyncing,
  };
}
