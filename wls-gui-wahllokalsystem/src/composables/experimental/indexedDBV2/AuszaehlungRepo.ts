import type { FetchStateEnum } from "@/composables/experimental/indexedDBV2/fetchStateEnum.ts";
import type { IndexedDBItem } from "@/composables/experimental/indexedDBV2/indexDBItem.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import localforage from "localforage";

import { createIndexedDBItem } from "@/composables/experimental/indexedDBV2/indexDBItem.ts";

const KEY_PREFIX_TEAMSTATUS = "teamstatus__";

export function useAuszaehlungRepo(wahlID: string, wahlbezirkID: string) {
  const dbInstance = localforage.createInstance({
    driver: localforage.INDEXEDDB,
    name: "wahldb",
    storeName: `auszaehlung_${wahlID}_${wahlbezirkID}`,
  });

  async function getTeamStatus(
    teamID: string
  ): Promise<IndexedDBItem<StimmzettelerfassungTeamStatus> | null> {
    return _getItem(`${KEY_PREFIX_TEAMSTATUS}${teamID}`);
  }

  async function setTeamStatus(
    teamID: string,
    teamStatus: StimmzettelerfassungTeamStatus,
    fetchState: FetchStateEnum
  ): Promise<void> {
    const indexedDBItem = createIndexedDBItem(teamStatus, fetchState);
    await _setItem(`${KEY_PREFIX_TEAMSTATUS}${teamID}`, indexedDBItem);
  }

  async function replaceTeamStatus(
    teamID: string,
    teamStatus: StimmzettelerfassungTeamStatus,
    fetchState: FetchStateEnum
  ): Promise<IndexedDBItem<StimmzettelerfassungTeamStatus> | null> {
    const oldIndexedDBItem = await _getItem<StimmzettelerfassungTeamStatus>(
      `${KEY_PREFIX_TEAMSTATUS}${teamID}`
    );
    await setTeamStatus(teamID, teamStatus, fetchState);
    return oldIndexedDBItem;
  }

  async function _getItem<T>(key: string): Promise<IndexedDBItem<T> | null> {
    return (await dbInstance.getItem(key)) as IndexedDBItem<T>;
  }

  async function _setItem<T>(key: string, value: T): Promise<void> {
    await dbInstance.setItem(key, value);
  }

  return {
    getTeamStatus,
    replaceTeamStatus,
    setTeamStatus,
  };
}
