import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";

const { logError } = useLogging(useIndexDB.name);

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
  });

  async function getItemFromIDB(key: string): Promise<IndexDBValue | null> {
    try {
      return await localforage.getItem<IndexDBValue>(key);
    } catch (error) {
      logError("Fehler beim Laden aus IDB:", error);
      return null;
    }
  }

  async function getDirtyItems(cKey: CryptoKey | undefined, vector: Uint8Array<ArrayBuffer>) {
    const matchingItems: {
      key: string;
      item: IndexDBValue;
    }[] = [];
    console.debug("CryptoKey start getDirtyItems: ", cKey);
    await localforage.iterate((value: IndexDBValue, key) => {
      if (value.dirty === true) {
        matchingItems.push({
          key,
          item: value,
        });
      }
    });

    console.debug("MachtingItems vor der Entschlüsselung: ", matchingItems);
    let matchingItemsWithDecryptedData: {
      key: string;
      item: IndexDBValue;
    }[] = [];
    if (cKey) {
      console.debug("ckey vorhanden: ", cKey);
      matchingItemsWithDecryptedData = await Promise.all(matchingItems.map(async value => ({
        key: value.key,
        item: {
          ...value.item,
          data: await _decryptData(value.item.data, cKey, vector)
        }
      })));

    }
    console.debug("MachtingItems nach der Entschlüsselung: ", matchingItemsWithDecryptedData);

    return matchingItemsWithDecryptedData;
  }

  async function storeItem(key: string, data: IndexDBValue) {
    const encrypted = await encrypt(data.data?.toString(), cryptoKey);
    console.debug("Encrypted: ", encrypted);

    await localforage.setItem(key, {...data, data: encrypted});
  }

  async function encrypt(data: string | undefined, key: CryptoKey) {
    console.debug("Encrypt with pin: ", key);
    console.debug("Encrypted with iv: ", iv);
    return await crypto.subtle.encrypt(
      { name: "AES-GCM", iv },
      key,
      new TextEncoder().encode(data)
    );
  }

  async function decrypt(data: ArrayBuffer, key: CryptoKey, vector: Uint8Array<ArrayBuffer>) {
    console.debug("Decrypt with pin: ", key);
    console.debug("Decrypted with iv: ", vector);
    return await crypto.subtle.decrypt(
      { name: "AES-GCM", iv: vector },
      key,
      data
    );
  }

  async function _decryptData(data: ArrayBuffer | string | null, key: CryptoKey, vector: Uint8Array<ArrayBuffer>) {
    let dataBuffer: ArrayBuffer;
    if (typeof data === "string") {
      dataBuffer = _base64ToArrayBuffer(data);
    } else {
      dataBuffer = data ?? new ArrayBuffer();
    }
    console.debug("vor decryptData: ", dataBuffer);
    const result = await decrypt(dataBuffer, key, vector);
    console.debug("Decrypted: ", new TextDecoder('utf-8').decode(result));
    return new TextDecoder('utf-8').decode(result);
  }

  function _base64ToArrayBuffer(base64: string): ArrayBuffer {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
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
