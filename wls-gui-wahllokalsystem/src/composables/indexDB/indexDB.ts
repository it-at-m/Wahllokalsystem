import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";
import { useCryptoUtils } from "@/composables/crypto/cryptoUtils.ts";

const { logError } = useLogging(useIndexDB.name);
const { encrypt, decrypt } = useCryptoUtils();

export function useIndexDB() {
  let cryptoKey: CryptoKey;
  let iv: Uint8Array<ArrayBuffer>;

  self.addEventListener("message", function (event) {
    if (event.data.type === "PIN") {
      cryptoKey = event.data.payload;
    }
    if (event.data.type === "IV") {
      iv = event.data.payload;
    }
    console.debug("Event Listener: ", cryptoKey, iv);
  });

  async function getItemFromIDB(key: string): Promise<IndexDBValue | null> {
    try {
      return await localforage.getItem<IndexDBValue>(key);
    } catch (error) {
      logError("Fehler beim Laden aus IDB:", error);
      return null;
    }
  }

  async function getDirtyItems(
    cKey: CryptoKey,
    vector: Uint8Array<ArrayBuffer>
  ) {
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

    return await Promise.all(
      matchingItems.map(async (value) => ({
        key: value.key,
        item: {
          ...value.item,
          data: await decrypt(value.item.data, cKey, vector),
        },
      }))
    );
  }

  async function storeItem(key: string, data: IndexDBValue) {
    const encryptedData = await encrypt(data.data?.toString(), cryptoKey, iv);
    await localforage.setItem(key, { ...data, data: encryptedData });
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
