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

interface KeyProducer<T> {
  produceKey: (entity: T) => string;
}
interface KeyExtractor<T> {
  extractKey: (key: string) => T;
}

const StimmzettelKeyProducer: KeyProducer<StimmzettelKey> &
  KeyExtractor<StimmzettelKey> = {
  produceKey: (entity) => {
    return JSON.stringify(entity, ["teamID", "kennung"]);
  },
  extractKey: (key: string) => {
    return JSON.parse(key) as StimmzettelKey;
  },
};

interface StimmzettelKey {
  teamID: string;
  kennung: number;
}

export function useStimmzettelRepo(wahlID: string, wahlbezirkID: string) {
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

  async function getAllByTeam(teamID: string) {
    const result: OfflineCachedResource<Stimmzettel>[] = [];
    await dbInstance.iterate((value, key) => {
      const parsedKey = StimmzettelKeyProducer.extractKey(key);
      if (parsedKey.teamID === teamID) {
        result.push(value as OfflineCachedResource<Stimmzettel>);
      }
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
    getAllByTeam,
    save,
    saveAll,
  };
}

export function useExperimentalStimmzettelService(
  wahlID: string,
  wahlbezirkID: string
) {
  const { getAll, getAllByTeam, save, saveAll } = useStimmzettelRepo(
    wahlID,
    wahlbezirkID
  );
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
    const stimmzettelFromIndexDB = await getAllByTeam(teamID);
    if (stimmzettelFromIndexDB.length > 0) {
      return stimmzettelFromIndexDB.map((i) => i.resource);
    }
    return await _fetchStoreAndReturnStimmzettel(teamID, true); //TODO nur machen wenn nicht weiß ob die Daten in der DB stimmen
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
