import type { SyncTask } from "@/composables/experimental/experimentalStimmzettelService.ts";
import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/fetchStateEnum.ts";
import type { IndexedDBItem } from "@/composables/experimental/indexedDBV2/indexDBItem.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import localforage from "localforage";

import { isTransfered } from "@/composables/experimental/indexedDBV2/fetchStateEnum.ts";
import { createIndexedDBItem } from "@/composables/experimental/indexedDBV2/indexDBItem.ts";

type TeamStatusIndexedDBItem = IndexedDBItem<
  string,
  StimmzettelerfassungTeamStatus
>;

export function useAuszaehlungRepo(wahlID: string, wahlbezirkID: string) {
  const dbInstance = localforage.createInstance({
    driver: localforage.INDEXEDDB,
    name: "wahldb",
    storeName: `teamStatus_${wahlID}_${wahlbezirkID}`,
  });

  async function getAllTeamStatus(): Promise<TeamStatusIndexedDBItem[]> {
    const result: TeamStatusIndexedDBItem[] = [];
    await dbInstance.iterate((value: TeamStatusIndexedDBItem) => {
      //TODO validate
      if (value.item) {
        result.push(value);
      }
    });

    return result;
  }

  async function getTeamStatus(
    teamID: string
  ): Promise<TeamStatusIndexedDBItem | null> {
    return _getItem(teamID);
  }

  async function setTeamStatus(
    teamID: string,
    teamStatus: StimmzettelerfassungTeamStatus | null,
    fetchState: FetchStateEnum
  ): Promise<void> {
    const indexedDBItem = createIndexedDBItem(teamID, teamStatus, fetchState);
    await _setItem(teamID, indexedDBItem);
  }

  async function replaceTeamStatus(
    teamID: string,
    teamStatus: StimmzettelerfassungTeamStatus | null,
    fetchState: FetchStateEnum
  ): Promise<TeamStatusIndexedDBItem | null> {
    const oldIndexedDBItem =
      await _getItem<StimmzettelerfassungTeamStatus>(teamID);
    await setTeamStatus(teamID, teamStatus, fetchState);
    return oldIndexedDBItem;
  }

  async function getDirtyTeamStatusRequests() {
    const allItems = await getAllTeamStatus();
    return allItems
      .filter((item) => !isTransfered(item.metaData.fetchState))
      .filter((item) => item.item !== null);
  }

  async function _getItem<T>(
    key: string
  ): Promise<IndexedDBItem<string, T> | null> {
    return (await dbInstance.getItem(key)) as IndexedDBItem<string, T>;
  }

  async function _setItem<T>(key: string, value: T): Promise<void> {
    await dbInstance.setItem(key, value);
  }

  return {
    getDirtyTeamStatusRequests,
    getTeamStatus,
    replaceTeamStatus,
    setTeamStatus,
  };
}
