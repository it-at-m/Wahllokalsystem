import axios from "axios";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const { getDirtyItems } = useIndexDB();
const { logDebug } = useLogging(`useDataSyncer`);

export function useDataSyncer() {
  async function syncDirtyData() {
    const itemsToSync = await getDirtyItems();
    for (const item of itemsToSync) {
      logDebug(`syncing item with key ${item.key}`);
      await axios.request(
        basicPostConfig(
          item.key,
          FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          JSON.parse(item.item.data)
        )
      );
    }
  }

  return {
    syncDirtyData,
  };
}
