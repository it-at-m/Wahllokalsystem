import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/fetchStateEnum.ts";

export interface ItemMetaData {
  fetchState: FetchStateEnum;
}

export function isItemMetaData(item: unknown): item is ItemMetaData {
  return typeof item === "object" && item !== null && "fetchState" in item;
}
