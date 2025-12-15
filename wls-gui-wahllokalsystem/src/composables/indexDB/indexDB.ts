import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";
import { useCryptoUtils } from "@/composables/crypto/cryptoUtils.ts";

export interface IndexDBComposable {
  cryptoKey: CryptoKey | null;
  setKey: (key: CryptoKey) => void;
  getItemFromIDB: (key: string) => Promise<IndexDBValue | null>;
  getDirtyItems: () => Promise<
    {
      key: string;
      item: IndexDBValue;
    }[]
  >;
  storeItem: (key: string, data: IndexDBValue) => Promise<void>;
  setupIndexDB: () => void;
}

let instance: IndexDBComposable | null = null;

export const useIndexDB = () => {
  const { logError } = useLogging("useIndexDB");
  const { encrypt, decrypt } = useCryptoUtils();

  if (!instance) {
    instance = {
      cryptoKey: null as CryptoKey | null,

      setKey(key: CryptoKey) {
        if (!key || !(key instanceof CryptoKey)) {
          throw new Error("CryptoKey kann nicht gesetzt werden.");
        }
        this.cryptoKey = key;
      },

      async getItemFromIDB(key: string): Promise<IndexDBValue | null> {
        try {
          const item = await localforage.getItem<IndexDBValue>(key);
          if (item) {
            return item.data
              ? ({
                  ...item,
                  data: await decrypt(item.data, this.cryptoKey),
                } as IndexDBValue)
              : item;
          } else {
            return null;
          }
        } catch (error) {
          logError("Fehler beim Laden aus IDB:", error);
          return null;
        }
      },

      async getDirtyItems() {
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
          matchingItems.map(async (value) => {
            try {
              return {
                key: value.key,
                item: {
                  ...value.item,
                  data: await decrypt(value.item.data, this.cryptoKey),
                },
              };
            } catch (error) {
              logError(
                `Fehler beim Entschlüsseln von Item ${value.key}:`,
                error
              );
              throw error;
            }
          })
        );
      },

      async storeItem(key: string, data: IndexDBValue) {
        try {
          if (data.data === null) {
            await localforage.setItem(key, data);
          } else {
            await localforage.setItem(key, {
              ...data,
              data: await encrypt(data.data?.toString(), this.cryptoKey),
            });
          }
        } catch (error) {
          logError(`Fehler beim Speichern von Item ${key}:`, error);
          throw error;
        }
      },

      setupIndexDB() {
        localforage.config({
          driver: localforage.INDEXEDDB,
          name: "wahldb",
          version: 1.0,
          storeName: "wahlstore",
          description: "store for data of electoral district",
        });
      },
    };
  }
  return instance;
};
