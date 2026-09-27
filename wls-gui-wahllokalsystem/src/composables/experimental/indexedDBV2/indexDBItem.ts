import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/fetchStateEnum.ts";
import type { ItemMetaData } from "@/composables/experimental/indexedDBV2/ItemMetaData.ts";

import { isItemMetaData } from "@/composables/experimental/indexedDBV2/ItemMetaData.ts";

export interface IndexedDBItem<KEY_TYPE, ITEM_TYPE> {
  item: ITEM_TYPE;
  key: KEY_TYPE;
  metaData: ItemMetaData;
}

export function assertIsIndexedDBItem(
  item: unknown
): item is IndexedDBItem<unknown, unknown> {
  return (
    typeof item === "object" &&
    item !== null &&
    "item" in item &&
    "metaData" in item &&
    "key" in item &&
    isItemMetaData(item.metaData)
  );
}

export function createIndexedDBItem<KEY_TYPE, ITEM_TYPE>(
  key: KEY_TYPE,
  item: ITEM_TYPE,
  fetchState: FetchStateEnum
): IndexedDBItem<KEY_TYPE, ITEM_TYPE> {
  return {
    item,
    key,
    metaData: {
      fetchState: fetchState,
    },
  };
}
