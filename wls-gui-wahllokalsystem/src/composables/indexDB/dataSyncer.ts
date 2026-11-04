import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";
import type { SyncronizeDataResult } from "@/types/indexDB/SyncronizeDataResult.ts";
import type { Task } from "@/types/tasks/Task.ts";

import axios from "axios";
import { computed, ref } from "vue";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useNumberTools } from "@/composables/common/numberTools.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useIndexDBUtils } from "@/composables/indexDB/indexDBUtils.ts";
import { INDEXED_DB_URL_HANDLERS } from "@/composables/indexDB/indexedDBUrlHandlers.ts";
import { useTaskManager } from "@/composables/tasks/taskManager.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const indexDBSingleton = useIndexDB();
const { compareByTimestamp } = useIndexDBUtils();
const { logDebug } = useLogging("dataSyncer");
const { groupConsecutiveNumbers } = useNumberTools();

export function useDataSyncer() {
  const taskManager = useTaskManager();
  const isOfflineDataSyncing = ref(false);
  const lastSyncUpdateTime = ref<null | Date>(null);
  const dirtyTasksAfterSync = ref<Task[] | null>(null);

  const numberOfDirtyTasksAfterSync = computed(
    (): number | undefined => dirtyTasksAfterSync.value?.length
  );

  const dirtyIDBTaskLabels = computed(() => {
    const tasks = dirtyTasksAfterSync.value ?? [];

    if (tasks.length > 0) {
      logDebug(
        "Dirty Tasks nach Sync:",
        tasks.map((task) => task.name)
      );
    }

    const stimmzettelKennungen = [];
    const taskLabels = new Set<string>();

    for (const task of tasks) {
      const label = _mapIndexedDBUrlToLabel(task.name);

      const stimmzettelLabelMatch = /^Stimmzettel (\d+)$/.exec(label);
      if (stimmzettelLabelMatch) {
        stimmzettelKennungen.push(Number(stimmzettelLabelMatch[1]));
      } else {
        taskLabels.add(label);
      }
    }
    stimmzettelKennungen.sort((a, b) => a - b);

    if (stimmzettelKennungen.length > 0) {
      const groupedJoinedKennungen =
        groupConsecutiveNumbers(stimmzettelKennungen).join(", ");
      taskLabels.add(`Stimmzettel ${groupedJoinedKennungen}`);
    }

    return [...taskLabels];
  });

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

  function _mapIndexedDBUrlToLabel(url: string): string {
    const indexedDBUrl = new URL(url);
    const handler = INDEXED_DB_URL_HANDLERS.find((candidate) =>
      candidate.canHandle(indexedDBUrl)
    );

    /** fallback to "Administration" for these endpoints:
     * /authserver/logouturl
     * /deleteWahllokalbenutzer/{wahltagID}
     * /demo
     * /exportWahllokalbenutzer/{wahltagID}
     * /generateAndExportWahllokalbenutzer/{wahltagID}
     * /mbw/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/bedenklicheStimmzettel
     * /oauthsessions/
     * /oauthsessions/{sessionID}/invalidate
     * /roles/mappings
     * /user
     * /user/{username}/unlock
     */
    return handler?.getLabel(indexedDBUrl) ?? "Administration";
  }

  return {
    ...taskManager,
    getSyncTasks,
    synchronizeOfflineData,
    numberOfDirtyTasksAfterSync,
    lastSyncUpdateTime,
    isOfflineDataSyncing,
    dirtyTasksAfterSync,
    dirtyIDBTaskLabels,
  };
}
