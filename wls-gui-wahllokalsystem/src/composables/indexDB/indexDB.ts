import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";

const { logError, log } = useLogging(useIndexDB.name);

export function useIndexDB() {
  async function getItemFromIDB(key: string): Promise<IndexDBValue | null> {
    try {
      return await localforage.getItem<IndexDBValue>(key);
    } catch (error) {
      logError("Fehler beim Laden aus IDB:", error);
      return null;
    }
  }

  async function getDirtyItems() {
    const matchingItems: {
      key: string;
      item: IndexDBValue;
    }[] = [];

    await localforage.iterate((value: IndexDBValue, key) => {
      if (value.dirty === true) {
        matchingItems.push({
          key,
          item: value,
        });
      }
    });

    return matchingItems;
  }

  async function markAsClean(key: string) {
    const item = await getItemFromIDB(key);
    if (item) {
      item.dirty = false;
      await storeItem(key, item);
    }
  }

  function setItemInIDB(
    key: string,
    data: unknown,
    url: string,
    dirty: boolean
  ) {
    log("saving data - value: " + JSON.stringify(data) + ", dirty: " + dirty);
    const value = { data: data, url: url, dirty: dirty };
    return localforage.setItem(key, value);
  }

  async function storeItem(key: string, data: IndexDBValue) {
    await localforage.setItem(key, data);
  }

  function setupIndexDB() {
    localforage.config({
      driver: localforage.INDEXEDDB, // Force WebSQL; same as using setDriver()
      name: "wahldb",
      version: 1.0,
      storeName: "wahlstore",
      description: "store for wahlnumber",
    });
  }

  return {
    getDirtyItems,
    getItemFromIDB,
    markAsClean,
    setItemInIDB,
    setupIndexDB,
    storeItem,
  };
}
