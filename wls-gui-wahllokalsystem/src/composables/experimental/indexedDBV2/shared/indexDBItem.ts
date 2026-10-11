import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/shared/fetchStateEnum.ts";
import type { ItemMetaData } from "@/composables/experimental/indexedDBV2/shared/ItemMetaData.ts";

import { isItemMetaData } from "@/composables/experimental/indexedDBV2/shared/ItemMetaData.ts";

export interface IndexedDBItem<ITEM_TYPE> {
  item: ITEM_TYPE;
  metaData: ItemMetaData;
}

export interface IndexedDBItemWithKey<
  KEY_TYPE,
  ITEM_TYPE,
> extends IndexedDBItem<ITEM_TYPE> {
  key: KEY_TYPE;
}

export function assertIsIndexedDBItem(
  item: unknown
): item is IndexedDBItem<unknown> {
  return (
    typeof item === "object" &&
    item !== null &&
    "item" in item &&
    "metaData" in item &&
    isItemMetaData(item.metaData)
  );
}

export function createIndexedDBItem<ITEM_TYPE>(
  item: ITEM_TYPE,
  fetchState: FetchStateEnum
): IndexedDBItem<ITEM_TYPE> {
  return {
    item,
    metaData: {
      fetchState: fetchState,
    },
  };
}

export function createIndexedDBItemWithKey<KEY_TYPE, ITEM_TYPE>(
  key: KEY_TYPE,
  item: ITEM_TYPE,
  fetchState: FetchStateEnum
): IndexedDBItemWithKey<KEY_TYPE, ITEM_TYPE> {
  return {
    key,
    ...createIndexedDBItem(item, fetchState),
  };
}
