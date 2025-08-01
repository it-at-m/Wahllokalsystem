import localforage from "localforage";

export function useIndexDB() {
  async function getItemFromIDB<T>(key: string): Promise<T | null> {
    try {
      return await localforage.getItem<T>(key);
    } catch (error) {
      console.error("Fehler beim Laden aus IDB:", error);
      return null;
    }
  }

  function setItemInIDB(
    key: string,
    data: unknown,
    url: string,
    dirty: boolean
  ) {
    console.log(
      "saving data - value: " + JSON.stringify(data) + ", dirty: " + dirty
    );
    const value = { data: data, url: url, dirty: dirty };
    return localforage.setItem(key, value);
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
  };
}
