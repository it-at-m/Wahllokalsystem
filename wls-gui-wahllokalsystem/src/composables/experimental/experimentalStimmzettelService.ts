import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import localforage from "localforage";
import { toRaw } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";

//https://mdn.github.io/dom-examples/indexeddb-examples/idbindex/
//https://github.com/mdn/dom-examples/blob/main/indexeddb-examples/idbindex/scripts/main.js
//https://developer.mozilla.org/en-US/docs/Web/API/IDBIndex

export interface SyncTask {
  name: string;
  callback: () => Promise<void>;
}

export interface SyncAdapter {
  getTasks: () => Promise<SyncTask[]>;
}

export const FetchStateEnum = {
  PENDING: "PENDING",
  TRANSMITTED: "TRANSMITTED",
  DIRTY: "DIRTY",
} as const;
export type FetchState = (typeof FetchStateEnum)[keyof typeof FetchStateEnum];

interface OfflineCachedResource<T> {
  resource: T;
  fetchState: FetchState | undefined;
}

export function useOfflineCachedRessourceTools() {
  function createDirtyRessource<T>(resource: T): OfflineCachedResource<T> {
    return {
      resource: _asRaw(resource),
      fetchState: FetchStateEnum.DIRTY,
    };
  }

  function createPendingRessource<T>(resource: T): OfflineCachedResource<T> {
    return {
      resource: _asRaw(resource),
      fetchState: FetchStateEnum.PENDING,
    };
  }

  function createTransmittedRessource<T>(
    resource: T
  ): OfflineCachedResource<T> {
    return {
      resource: _asRaw(resource),
      fetchState: FetchStateEnum.TRANSMITTED,
    };
  }

  function createReceivedRessource<T>(resource: T): OfflineCachedResource<T> {
    return {
      resource: _asRaw(resource),
      fetchState: undefined,
    };
  }

  function _asRaw<T>(ressource: T): T {
    return toRaw(ressource);
  }

  return {
    createDirtyRessource,
    createPendingRessource,
    createReceivedRessource,
    createTransmittedRessource,
  };
}

interface ReadAllRepository<T> {
  getAll: () => Promise<OfflineCachedResource<T>[]>;
}

interface WriteResourceRepository<T> {
  save: (cachedRessource: OfflineCachedResource<T>) => Promise<void>;
}
interface WriteAllRepository<T> {
  saveAll: (cachedRessource: OfflineCachedResource<T>[]) => Promise<void>;
}

interface KeyProducer<T> {
  produceKey: (entity: T) => string;
}

const StimmzettelKeyProducer: KeyProducer<StimmzettelKey> = {
  produceKey: (entity) => `${entity.teamID}_${entity.kennung}`,
};

interface StimmzettelKey {
  teamID: string;
  kennung: number;
}

export function useStimmzettelRepo(
  wahlID: string,
  wahlbezirkID: string
): WriteResourceRepository<Stimmzettel> &
  ReadAllRepository<Stimmzettel> &
  WriteAllRepository<Stimmzettel> {
  const dbInstance = localforage.createInstance({
    driver: localforage.INDEXEDDB,
    name: "wahldb",
    storeName: `stimmzettel_${wahlID}_${wahlbezirkID}`,
  });

  async function getAll() {
    const result: OfflineCachedResource<Stimmzettel>[] = [];
    await dbInstance.iterate((value) => {
      //TODO validate item
      result.push(value as OfflineCachedResource<Stimmzettel>);
    });
    return result;
  }

  async function saveAll(ressources: OfflineCachedResource<Stimmzettel>[]) {
    const savePromises: Promise<void>[] = [];
    ressources.forEach((ressource) => savePromises.push(save(ressource)));

    await Promise.allSettled(savePromises);
  }

  async function save(
    offlineCachedStimmzettel: OfflineCachedResource<Stimmzettel>
  ) {
    const key: StimmzettelKey = {
      kennung: offlineCachedStimmzettel.resource.stimmzettelkennung,
      teamID: offlineCachedStimmzettel.resource.teamID,
    };
    await dbInstance.setItem(
      StimmzettelKeyProducer.produceKey(key),
      toRaw(offlineCachedStimmzettel)
    );
  }

  return {
    getAll,
    save,
    saveAll,
  };
}

export function useExperimentalStimmzettelService(
  wahlID: string,
  wahlbezirkID: string
) {
  const { getAll, save, saveAll } = useStimmzettelRepo(wahlID, wahlbezirkID);
  const {
    getStimmzettel: fetchStimmzettel,
    saveSingleStimmzettel,
    getAnzahlStimmzettel,
  } = useStimmzettelService();
  const {
    createDirtyRessource,
    createPendingRessource,
    createReceivedRessource,
    createTransmittedRessource,
  } = useOfflineCachedRessourceTools();

  async function getTasksToSync(): Promise<SyncTask[]> {
    const allItems = await getAll();
    const dirtyItems = allItems.filter(
      (item) => item.fetchState === FetchStateEnum.DIRTY
    );
    return dirtyItems.map(
      (item) =>
        ({
          name: `stimmzettel ${item.resource.stimmzettelkennung} von ${item.resource.teamID} wird gespeichert`,
          callback: () => _transmitStimmzettelAndStoreResult(item.resource),
        }) as SyncTask
    );
  }

  async function initOfflineCachedStimmzettel(teamID: string) {
    const existingStimmzettel = await getAll();
    if (existingStimmzettel.length === 0) {
      await _fetchStoreAndReturnStimmzettel(teamID, false);
    }
  }

  async function getStimmzettel(teamID: string): Promise<Stimmzettel[]> {
    const stimmzettelFromIndexDB = await getAll();
    if (stimmzettelFromIndexDB.length > 0) {
      return stimmzettelFromIndexDB.map((i) => i.resource); //TODO Filter auf Team
    }
    return await _fetchStoreAndReturnStimmzettel(teamID, true);
  }

  async function saveStimmzettel(stimmzettel: Stimmzettel) {
    await save(createPendingRessource(stimmzettel));
    _transmitStimmzettelAndStoreResult(stimmzettel);
  }

  async function _transmitStimmzettelAndStoreResult(stimmzettel: Stimmzettel) {
    try {
      await saveSingleStimmzettel(wahlID, wahlbezirkID, stimmzettel);
      await save(createTransmittedRessource(stimmzettel));
    } catch {
      await save(createDirtyRessource(stimmzettel));
    }
  }

  async function _fetchStoreAndReturnStimmzettel(
    teamID: string,
    sendNotification: boolean
  ): Promise<Stimmzettel[]> {
    const fetchedStimmzettel = await fetchStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      sendNotification
    );
    const offlineRessources = fetchedStimmzettel.map((stimmzettel) =>
      createReceivedRessource(stimmzettel)
    );
    await saveAll(offlineRessources);
    return fetchedStimmzettel;
  }

  return {
    initOfflineCachedStimmzettel,
    saveStimmzettel,
    getAnzahlStimmzettel: () => getAnzahlStimmzettel(wahlID, wahlbezirkID),
    getStimmzettel,
    getTasks: getTasksToSync,
  };
}
