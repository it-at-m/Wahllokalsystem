import type {
  SyncAdapter,
  SyncTask,
} from "@/composables/experimental/experimentalStimmzettelService.ts";
import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";
import type { SyncronizeDataResult } from "@/types/indexDB/SyncronizeDataResult.ts";
import type { Task } from "@/types/tasks/Task.ts";

import axios from "axios";
import { computed, ref } from "vue";

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
  const dirtyTasksAfterSync = ref<Task[] | null>(null);
  const syncAdapter = ref<SyncAdapter[]>([]);

  const numberOfDirtyTasksAfterSync = computed(
    (): number | undefined => dirtyTasksAfterSync.value?.length
  );

  async function getSyncTasks() {
    const tasksFromAdapter: SyncTask[] = [];
    await Promise.allSettled(
      syncAdapter.value.map(async (adapter) =>
        tasksFromAdapter.push(...(await adapter.getTasks()))
      )
    );

    const commonDirtyItemsToSync = await indexDBSingleton.getDirtyItems();
    commonDirtyItemsToSync.sort(_compareSyncItemByTimeStamp);
    const commonDirtyItemsSyncTasks = commonDirtyItemsToSync.map((item) => ({
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

    return [...commonDirtyItemsSyncTasks, ...tasksFromAdapter];
  }

  async function synchronizeOfflineData(): Promise<SyncronizeDataResult | null> {
    if (isOfflineDataSyncing.value) return null;

    isOfflineDataSyncing.value = true;
    try {
      taskManager.setTasks(await getSyncTasks());
      await taskManager.runAllTasks();
    } catch (e) {
      logDebug("Fehler beim Synchronisieren", e);
    } finally {
      isOfflineDataSyncing.value = false;
      lastSyncUpdateTime.value = new Date();
      dirtyTasksAfterSync.value = await getSyncTasks();
    }

    return {
      numberOfTasksRan: taskManager.numberOfTasksToRun.value,
      numberOfTasksFailed: taskManager.numberOfTasksFailed.value,
      numberOfTasksSucceeded: taskManager.numberOfTasksSucceeded.value,
      numberOfDirtyTasksRemaining: dirtyTasksAfterSync.value.length,
    };
  }

  function registerSyncAdapter(adapter: SyncAdapter) {
    syncAdapter.value.push(adapter);
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
    registerSyncAdapter,
    synchronizeOfflineData,
    numberOfDirtyTasksAfterSync,
    lastSyncUpdateTime,
    isOfflineDataSyncing,
    dirtyTasksAfterSync,
  };
}
