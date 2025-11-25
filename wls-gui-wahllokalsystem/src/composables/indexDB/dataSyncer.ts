import type {IndexDBValue} from "@/types/indexDB/IndexDBValue.ts";

import axios from "axios";

import {basicPostConfig} from "@/api/axios-utils.ts";
import {useIndexDB} from "@/composables/indexDB/indexDB.ts";
import {useIndexDBUtils} from "@/composables/indexDB/indexDBUtils.ts";
import {FetchStrategiesEnum} from "@/types/api/FetchStrategiesEnum.ts";
import {useCryptoUtils} from "@/composables/crypto/cryptoUtils.ts";

const { getDirtyItems, decryptData } = useIndexDB();
const { compareByTimestamp } = useIndexDBUtils();
const { decrypt } = useCryptoUtils();

export function useDataSyncer() {
  async function getSyncTasks() {
    const itemsToSync = await getDirtyItems();
    /*console.debug("Vor entschlüsselung: ", itemsToSync[0]?.item.data);
    itemsToSync.map(value => ({
      ...value,
      data: _decryptData(value.item.data)
    }));
    console.debug("Nach entschlüsselung: ", itemsToSync[0]?.item.data);*/
    /*for (const value of itemsToSync) {
      value.item.data = await _decryptData(value.item.data);
    }*/
    itemsToSync.sort(_compareSyncItemByTimeStamp);
    return itemsToSync.map((item) => ({
      name: item.key,
      callback: () =>
        axios.request(
          basicPostConfig(
            item.key,
            FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
            _parseDataBasedOnContentType(item.item.data)
          )
        ),
    }));
/*    return await Promise.all(
      itemsToSync.map(async (item) => {
        const modifiedItem = await decryptItem(item.item);
        return {
          name: item.key,
          callback: () =>
            axios.request(
              basicPostConfig(
                item.key,
                FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
                _parseDataBasedOnContentType(modifiedItem.data)
              )
            )
        };
      })
    );*/
  }

  /*async function decryptItem(item: IndexDBValue): Promise<IndexDBValue> {
    let dataBuffer: ArrayBuffer;
    if (typeof item.data === "string") {
      dataBuffer = _base64ToArrayBuffer(item.data);
    } else {
      dataBuffer = item.data ?? new ArrayBuffer();
    }

    const decryptedData = new TextDecoder('utf-8').decode(await decryptData(dataBuffer))
    return {...item, data: decryptedData};
  }*/

  async function _decryptData(data: ArrayBuffer | string | null) {
    let dataBuffer: ArrayBuffer;
    if (typeof data === "string") {
      dataBuffer = _base64ToArrayBuffer(data);
    } else {
      dataBuffer = data ?? new ArrayBuffer();
    }
    return new TextDecoder('utf-8').decode(await decryptData(dataBuffer)).toString();
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

  function _compareSyncItemByTimeStamp(
    a: { item: IndexDBValue },
    b: { item: IndexDBValue }
  ) {
    return compareByTimestamp(a.item, b.item);
  }

  function _parseDataBasedOnContentType(
    data: string | ArrayBuffer | null
  ): object | undefined {
    if (data && typeof data === "string") {
      return JSON.parse(data);
    } else if (data && data instanceof ArrayBuffer) {
      return data;
    } else {
      return undefined;
    }
  }

  return {
    getSyncTasks,
  };
}
