import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import axios from "axios";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useIndexDBUtils } from "@/composables/indexDB/indexDBUtils.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const { getDirtyItems } = useIndexDB();
const { compareByTimestamp } = useIndexDBUtils();

export function useDataSyncer() {
  async function getSyncTasks(
    cryptoKey: CryptoKey,
    vector: Uint8Array<ArrayBuffer>
  ) {
    const itemsToSync = await getDirtyItems(cryptoKey, vector);
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
    getSyncTasks,
  };
}
