import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/fetchStateEnum.ts";
import type { ItemMetaData } from "@/composables/experimental/indexedDBV2/ItemMetaData.ts";

import { isItemMetaData } from "@/composables/experimental/indexedDBV2/ItemMetaData.ts";

export interface IndexedDBItem<T> {
  item: T | null;
  metaData: ItemMetaData;
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

export function createIndexedDBItem<T>(
  item: T,
  fetchState: FetchStateEnum
): IndexedDBItem<T> {
  return {
    item,
    metaData: {
      fetchState: fetchState,
    },
  };
}
