import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

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
  saveById: (
    id: { teamID: string; kennung: number },
    entity: Stimmzettel
  ) => Promise<void>;
}

interface KeyProducer<T> {
  produceKey: (entity: T) => string;
}

export function useExperimentalStimmzettelService() {
  function getTasksToSync(): Promise<SyncTask[]> {
    return Promise.resolve([]);
  }

  function someMoreFunction() {
    return "hello world";
  }

  return {
    someMoreFunction,
    getTasks: getTasksToSync,
  };
}
