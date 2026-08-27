import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import localforage from "localforage";
import { unref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";

//https://mdn.github.io/dom-examples/indexeddb-examples/idbindex/
//https://github.com/mdn/dom-examples/blob/main/indexeddb-examples/idbindex/scripts/main.js
//https://developer.mozilla.org/en-US/docs/Web/API/IDBIndex

interface SyncTask {
  name: string;
  callback: () => Promise<void>;
}

interface SyncAdapter {
  getTasks: () => Promise<SyncTask[]>;
}

interface Repository {}

interface StimmzettelRepository extends Repository {
  saveById: (id: StimmzettelKey, entity: Stimmzettel) => Promise<void>;
  saveAll: (team: string, entities: Stimmzettel[]) => Promise<void>;
  getAll: () => Promise<Stimmzettel[]>;
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
): StimmzettelRepository {
  const dbInstance = localforage.createInstance({
    driver: localforage.INDEXEDDB,
    name: "stimmzettel",
    storeName: `${wahlID}_${wahlbezirkID}`,
  });

  async function getAll() {
    const result: Stimmzettel[] = [];
    await dbInstance.iterate((value) => {
      result.push(value as Stimmzettel);
    });
    return result;
  }

  async function saveAll(team: string, stimmzettelToSave: Stimmzettel[]) {
    stimmzettelToSave.forEach((stimmzettel) =>
      saveById(
        { teamID: team, kennung: stimmzettel.stimmzettelkennung },
        stimmzettel
      )
    );
  }

  async function saveById(key: StimmzettelKey, entity: Stimmzettel) {
    await dbInstance.setItem(
      StimmzettelKeyProducer.produceKey(key),
      toRaw(entity)
    );
  }

  return {
    getAll,
    saveById,
    saveAll,
  };
}

export function useExperimentalStimmzettelService(
  wahlID: string,
  wahlbezirkID: string
) {
  const { getAll, saveById, saveAll } = useStimmzettelRepo(
    wahlID,
    wahlbezirkID
  );
  const { getStimmzettel: fetchStimmzettel, saveSingleStimmzettel } =
    useStimmzettelService();

  function getTasksToSync(): Promise<SyncTask[]> {
    return Promise.resolve([]);
  }

  function someMoreFunction() {
    return "hello world";
  }

  async function getStimmzettel(
    teamID: string,
    sendNotification = true
  ): Promise<Stimmzettel[]> {
    const stimmzettelFromIndexDB = await getAll();
    if (stimmzettelFromIndexDB.length > 0) {
      return stimmzettelFromIndexDB; //TODO Filter auf Team
    }
    const fetchedStimmzettel = await fetchStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      sendNotification
    );
    await saveAll(teamID, fetchedStimmzettel);
    return fetchedStimmzettel;
  }

  async function saveStimmzettel(
    teamID: string,
    stimmzettelkennung: number,
    stimmzettel: Stimmzettel
  ) {
    //save in indexedDB for feedback
    await saveById(
      { kennung: stimmzettelkennung, teamID: teamID },
      unref(stimmzettel)
    ); //TODO Pending state
    //transmit and saveResult (dirty/clean)
    _transmitStimmzettelAndStoreResult(teamID, unref(stimmzettel));
  }

  async function _transmitStimmzettelAndStoreResult(
    teamID: string,
    stimmzettel: Stimmzettel
  ) {
    try {
      await saveSingleStimmzettel(wahlID, wahlbezirkID, teamID, stimmzettel);
      await saveById(
        { kennung: stimmzettel.stimmzettelkennung, teamID: teamID },
        stimmzettel
      ); //TODO CleanState
    } catch (e) {
      await saveById(
        { kennung: stimmzettel.stimmzettelkennung, teamID: teamID },
        stimmzettel
      ); //TODO: Dirty state
    }
  }

  return {
    saveStimmzettel,
    getStimmzettel,
    getTasks: getTasksToSync,
  };
}
