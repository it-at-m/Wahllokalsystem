import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";

const { logError } = useLogging(useIndexDB.name);

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

  async function storeItem(key: string, data: IndexDBValue) {
    await localforage.setItem(key, data);
  }

  function setupIndexDB() {
    localforage.config({
      driver: localforage.INDEXEDDB,
      name: "wahldb",
      version: 1.0,
      storeName: "wahlstore",
      description: "store for data of electoral district",
    });
  }

  return {
    getDirtyItems,
    getItemFromIDB,
    setupIndexDB,
    storeItem,
  };
}
