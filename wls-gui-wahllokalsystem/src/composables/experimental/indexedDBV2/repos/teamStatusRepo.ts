import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/shared/fetchStateEnum.ts";
import type {
  IndexedDBItem,
  IndexedDBItemWithKey,
} from "@/composables/experimental/indexedDBV2/shared/indexDBItem.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import localforage from "localforage";

import { isTransfered } from "@/composables/experimental/indexedDBV2/shared/fetchStateEnum.ts";
import { createIndexedDBItem } from "@/composables/experimental/indexedDBV2/shared/indexDBItem.ts";

type TeamStatusIndexedDBItem = IndexedDBItem<StimmzettelerfassungTeamStatus>;

type TeamStatusIndexedDBItemWithKey = IndexedDBItemWithKey<
  string,
  StimmzettelerfassungTeamStatus
>;

export function useTeamStatusRepo(wahlID: string, wahlbezirkID: string) {
  const dbInstance = localforage.createInstance({
    driver: localforage.INDEXEDDB,
    name: "wahldb",
    storeName: `teamStatus_${wahlID}_${wahlbezirkID}`,
  });

  async function deleteAllItems(): Promise<void> {
    await dbInstance.clear();
  }

  //TODO wir sollten Meta-Daten speichern ob Daten mal gelesen wurden. Wenn nicht wäre ein return `null` gut, sonst ein empty-Array?
  async function getAllTeamStatus(): Promise<TeamStatusIndexedDBItemWithKey[]> {
    const result: TeamStatusIndexedDBItemWithKey[] = [];
    await dbInstance.iterate((value: TeamStatusIndexedDBItem, key: string) => {
      //TODO validate
      if (value.item) {
        result.push({
          key,
          ...value,
        });
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
    const indexedDBItem = createIndexedDBItem(teamStatus, fetchState);
    await _setItem(teamID, indexedDBItem);
  }

  async function replaceTeamStatus(
    teamID: string,
    teamStatus: StimmzettelerfassungTeamStatus | null,
    fetchState: FetchStateEnum
  ): Promise<TeamStatusIndexedDBItem | null> {
    const oldIndexedDBItem = await _getItem(teamID);
    await setTeamStatus(teamID, teamStatus, fetchState);
    return oldIndexedDBItem;
  }

  async function getDirtyTeamStatusRequests() {
    const allItems = await getAllTeamStatus();
    return allItems
      .filter((item) => !isTransfered(item.metaData.fetchState))
      .filter((item) => item.item !== null);
  }

  async function _getItem(
    key: string
  ): Promise<IndexedDBItem<StimmzettelerfassungTeamStatus> | null> {
    return (await dbInstance.getItem(
      key
    )) as IndexedDBItem<StimmzettelerfassungTeamStatus>;
  }

  async function _setItem(
    key: string,
    value: IndexedDBItem<StimmzettelerfassungTeamStatus | null>
  ): Promise<void> {
    await dbInstance.setItem(key, value);
  }

  return {
    deleteAllItems,
    getDirtyTeamStatusRequests,
    getAllTeamStatus,
    getTeamStatus,
    replaceTeamStatus,
    setTeamStatus,
  };
}
