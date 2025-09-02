import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import axios from "axios";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const { getDirtyItems } = useIndexDB();

export function useDataSyncer() {
  async function getSyncTasks() {
    const itemsToSync = await getDirtyItems();
    itemsToSync.sort(_compareSyncItemByTimeStamp);
    return itemsToSync.map((item) => ({
      name: item.key,
      callback: () =>
        axios.request(
          basicPostConfig(
            item.key,
            FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
            item.item.data ? JSON.parse(item.item.data) : undefined
          )
        ),
    }));
  }

  function _compareSyncItemByTimeStamp(
    a: { item: IndexDBValue },
    b: { item: IndexDBValue }
  ) {
    if (a.item.timestamp === undefined && b.item.timestamp === undefined) {
      return 0;
    }

    if (a.item.timestamp === undefined) {
      return 1;
    }

    if (b.item.timestamp === undefined) {
      return -1;
    }

    return a.item.timestamp - b.item.timestamp;
  }

  return {
    getSyncTasks,
  };
}
