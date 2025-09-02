import axios from "axios";

import { basicPostConfig } from "@/api/axios-utils.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const { getDirtyItems } = useIndexDB();

export function useDataSyncer() {
  async function getSyncTasks() {
    const itemsToSync = await getDirtyItems();
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

  return {
    getSyncTasks,
  };
}
