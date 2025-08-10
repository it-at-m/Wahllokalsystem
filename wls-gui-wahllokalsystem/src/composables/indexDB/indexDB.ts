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
    getItemFromIDB,
    setItemInIDB,
    setupIndexDB,
    storeItem,
  };
}
