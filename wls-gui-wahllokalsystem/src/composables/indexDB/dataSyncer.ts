import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import axios from "axios";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useIndexDBUtils } from "@/composables/indexDB/indexDBUtils.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const { getDirtyItems } = useIndexDB();
const { compareByTimestamp } = useIndexDBUtils();

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
            item.item.data
              ? _parseDataBasedOnContentType(item.item.data)
              : undefined
          )
        ),
    }));
  }

  function _compareSyncItemByTimeStamp(
    a: { item: IndexDBValue },
    b: { item: IndexDBValue }
  ) {
    return compareByTimestamp(a.item, b.item);
  }

  function _parseDataBasedOnContentType(data: string | ArrayBuffer | null) {
    let convertedData;
    if (typeof data === "string") {
      convertedData = JSON.parse(data);
    } else if (data instanceof ArrayBuffer) {
      convertedData = data;
    }
    return convertedData;
  }

  return {
    getSyncTasks,
  };
}
