import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";
import {useCryptoUtils} from "@/composables/crypto/cryptoUtils.ts";

const { logError } = useLogging(useIndexDB.name);
//const { encrypt, decrypt } = useCryptoUtils();

export function useIndexDB() {
  const iv = crypto.getRandomValues(new Uint8Array(12));
  let cryptoKey: CryptoKey;

  self.addEventListener("message", async function (event) {
    if (event.data.type === "PIN") {
      console.debug("PIN: ", event.data.payload);
      cryptoKey = await _importKey(event.data.payload);
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

  async function getDirtyItems() {
    const matchingItems: {
      key: string;
      item: IndexDBValue;
    }[] = [];
    console.debug("CryptoKey start getDirtyItems: ", cryptoKey);
    await localforage.iterate((value: IndexDBValue, key) => {
      if (value.dirty === true) {
        matchingItems.push({
          key,
          item: value,
        });
      }
    });

    console.debug("MachtingItems vor der Entschlüsselung: ", matchingItems);
    await Promise.all(matchingItems.map(async value => ({
      ...value,
      data: await _decryptData(value.item.data)
    })));
    console.debug("MachtingItems nach der Entschlüsselung: ", matchingItems);

    return matchingItems;
  }

  async function storeItem(key: string, data: IndexDBValue) {
    const encrypted = await encrypt(data.data?.toString(), cryptoKey);
    console.debug("Encrypted: ", encrypted);
    const decrypted = await decrypt(encrypted, cryptoKey);
    console.debug("Decrypted: ", new TextDecoder('utf-8').decode(decrypted).toString());

    await localforage.setItem(key, {...data, data: encrypted});
  }

  async function encrypt(data: string | undefined, key: CryptoKey) {
    //const key = await _importKey(pin);
    console.debug("Encrypt with pin: ", key);
    return await crypto.subtle.encrypt(
      { name: "AES-GCM", iv },
      key,
      new TextEncoder().encode(data)
    );
  }

  async function decryptData(data: ArrayBuffer) {
    //return await decrypt(data, pin);
    //return await decrypt(data, "dummyPin");
    console.debug(cryptoKey);
    return await decrypt(data, cryptoKey);
  }

  async function decrypt(data: ArrayBuffer, key: CryptoKey) {
    //const key = await _importKey(pin);
    console.debug("Decrypt with pin: ", key);
    return await crypto.subtle.decrypt(
      { name: "AES-GCM", iv },
      key,
      data
    );
  }

  async function _decryptData(data: ArrayBuffer | string | null) {
    let dataBuffer: ArrayBuffer;
    if (typeof data === "string") {
      dataBuffer = _base64ToArrayBuffer(data);
    } else {
      dataBuffer = data ?? new ArrayBuffer();
    }
    console.debug("vor decryptData: ", dataBuffer);
    const result = await decryptData(dataBuffer);
    console.debug("Result: ", result);
    return new TextDecoder('utf-8').decode(result).toString();
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

  async function _importKey(password: string) {
    const keyMaterial = await crypto.subtle.importKey(
      "raw",
      new TextEncoder().encode(password),
      "PBKDF2",
      false,
      ["deriveBits", "deriveKey"]
    );

    const salt = crypto.getRandomValues(new Uint8Array(16));
    return await crypto.subtle.deriveKey(
      {
        name: "PBKDF2",
        salt: salt,
        iterations: 100000,
        hash: "SHA-256",
      },
      keyMaterial,
      { name: "AES-GCM", length: 256 },
      false,
      ["encrypt", "decrypt"]
    );
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
    decryptData,
  };
}
