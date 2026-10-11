import type {
  IndexedDBItem,
  IndexedDBItemWithKey,
} from "@/composables/experimental/indexedDBV2/shared/indexDBItem.ts";
import type { KeyProvider } from "@/composables/experimental/indexedDBV2/shared/keyProvider.ts";
import type { RepoMetaData } from "@/composables/experimental/indexedDBV2/shared/RepoMetaData.ts";

import localforage from "localforage";

import { useLogging } from "@/composables/common/logging.ts";
import { isTransfered } from "@/composables/experimental/indexedDBV2/shared/fetchStateEnum.ts";
import { metaDataKey } from "@/composables/experimental/indexedDBV2/shared/RepoMetaData.ts";

export function useAbstractRepo<KEY_TYPE, ITEM_TYPE>(
  storeName: string,
  options: {
    keyProvider: KeyProvider<KEY_TYPE>;
  }
) {
  const stimmzettelRepo = localforage.createInstance({
    driver: localforage.INDEXEDDB,
    name: "wahldb",
    storeName: storeName,
  });
  const logging = useLogging("abstractRepo");

  async function deleteAll() {
    logging.logDebug("deleteAll");
    await stimmzettelRepo.iterate(async (value, key) => {
      if (key !== metaDataKey) {
        await stimmzettelRepo.removeItem(key);
      }
    });

    await _setMetaData({ hasSavedAnyData: false });
  }

  async function deleteByKeyPredicate(predicate: (key: KEY_TYPE) => boolean) {
    logging.logDebug("deleteByKeyPredicate");
    const itemsToDelete = await getByKeyPredicate(predicate);
    logging.logDebug("itemsToDelete", itemsToDelete);
    await Promise.allSettled(
      itemsToDelete.map((item) =>
        stimmzettelRepo.removeItem(
          options.keyProvider.createLocalforageKey(item.key)
        )
      )
    );
  }

  async function getByKey(
    key: KEY_TYPE
  ): Promise<IndexedDBItem<ITEM_TYPE> | null> {
    logging.logDebug("getByKey", key);
    const indexDBItem = await stimmzettelRepo.getItem(
      options.keyProvider.createLocalforageKey(key)
    );
    logging.logDebug("getByKey result", indexDBItem);
    return indexDBItem as IndexedDBItem<ITEM_TYPE>;
  }

  async function getByKeyPredicate(
    predicate: (key: KEY_TYPE) => boolean
  ): Promise<IndexedDBItemWithKey<KEY_TYPE, ITEM_TYPE>[]> {
    logging.logDebug("getByKeyPredicate");
    const allItems = await getAllItems();

    const filteredItems = allItems.filter(
      (item) => item.key !== metaDataKey && predicate(item.key)
    );
    logging.logDebug("getByKeyPredicate result", filteredItems);
    return filteredItems;
  }

  async function getAllItems(): Promise<
    IndexedDBItemWithKey<KEY_TYPE, ITEM_TYPE>[]
  > {
    logging.logDebug("getAllItems");
    const allItems: IndexedDBItemWithKey<KEY_TYPE, ITEM_TYPE>[] = [];
    await stimmzettelRepo.iterate(
      (value: IndexedDBItem<ITEM_TYPE>, key: string) => {
        if (key !== metaDataKey) {
          allItems.push({
            key: options.keyProvider.readLocalforageKey(key),
            ...value,
          });
        }
      }
    );
    logging.logDebug("getAllItems result", allItems);
    return allItems;
  }

  async function getDirtyItems(): Promise<
    IndexedDBItemWithKey<KEY_TYPE, ITEM_TYPE>[]
  > {
    const allItems = await getAllItems();
    return allItems
      .filter((item) => !isTransfered(item.metaData.fetchState))
      .filter((item) => item.item !== null);
  }

  async function hadAnyData(): Promise<boolean> {
    const metaData = await _getMetaData();
    return metaData?.hasSavedAnyData ?? false;
  }

  async function setByKey(
    key: KEY_TYPE,
    value: IndexedDBItem<ITEM_TYPE> | null
  ) {
    logging.logDebug("setByKey", key, value);
    await stimmzettelRepo.setItem(
      options.keyProvider.createLocalforageKey(key),
      value
    );
    await _setMetaData({
      hasSavedAnyData: true,
    });
  }

  async function _getMetaData(): Promise<RepoMetaData | null> {
    const metaData = await stimmzettelRepo.getItem(metaDataKey);
    return metaData as RepoMetaData;
  }

  async function _setMetaData(metaData: RepoMetaData) {
    await stimmzettelRepo.setItem(metaDataKey, metaData);
  }

  return {
    deleteAll,
    deleteByKeyPredicate,
    getAllItems,
    getByKey,
    getByKeyPredicate,
    getDirtyItems,
    hadAnyData,
    setByKey,
  };
}
