import type { SyncTask } from "@/composables/experimental/experimentalStimmzettelService.ts";
import type { StimmzettelRepoKey } from "@/composables/experimental/indexedDBV2/repos/stimmzettel/stimmzettelRepoKey.ts";
import type { DataServiceOptionForceRefresh } from "@/composables/experimental/indexedDBV2/shared/DataServiceOptionForceRefresh.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useStimmzettelRepo } from "@/composables/experimental/indexedDBV2/repos/stimmzettel/stimmzettelRepo.ts";
import { getDefaultDataServiceOptionForceRefresh } from "@/composables/experimental/indexedDBV2/shared/DataServiceOptionForceRefresh.ts";
import { FetchStateEnum } from "@/composables/experimental/indexedDBV2/shared/fetchStateEnum.ts";
import { createIndexedDBItem } from "@/composables/experimental/indexedDBV2/shared/indexDBItem.ts";

export function useStimmzettelDataService(
  wahlID: string,
  wahlbezirkID: string
) {
  const logging = useLogging("useStimmzettelDataService");

  const stimmzettelRepo = useStimmzettelRepo(wahlID, wahlbezirkID);
  const stimmzettelService = useStimmzettelService();

  async function getTasksToSync(): Promise<SyncTask[]> {
    const dirtyStimmzettel = await stimmzettelRepo.getDirtyItems();
    return dirtyStimmzettel.map((item) => {
      const teamID = item.key.teamID;
      const stimmzettelkennung = item.key.stimmzettelkennung;
      return {
        name: `Stimmzettel ${teamID} ${stimmzettelkennung}`,
        callback: () => _sendAndStoreResult(item.item),
      };
    });
  }

  async function getStimmzettelOfTeam(
    teamID: string,
    options?: Partial<DataServiceOptionForceRefresh>
  ): Promise<PersistedStimmzettel[]> {
    const defaultOptions = getDefaultDataServiceOptionForceRefresh();
    const mergedOptions = { ...defaultOptions, ...options };

    if (mergedOptions.forceRefresh) {
      await _replaceStoredStimmzettelOfTeam(teamID);
    }

    const stimmzettelItems = await stimmzettelRepo.getByKeyPredicate(
      _createKeyPredicateTeamIdIsEqualTo(teamID)
    );
    return stimmzettelItems.map((item) => item.item);
  }

  async function loadStimmzettelOfTeam(teamID: string) {
    await _replaceStoredStimmzettelOfTeam(teamID);
  }

  async function setStimmzettel(stimmzettel: PersistedStimmzettel) {
    await stimmzettelRepo.setByKey(
      _createKey(stimmzettel),
      createIndexedDBItem(stimmzettel, FetchStateEnum.PENDING)
    );

    _sendAndStoreResult(stimmzettel);
  }

  function _createKey(stimmzettel: PersistedStimmzettel): StimmzettelRepoKey {
    return {
      teamID: stimmzettel.teamID,
      stimmzettelkennung: stimmzettel.stimmzettelkennung,
    };
  }

  function _createKeyPredicateTeamIdIsEqualTo(
    teamID: string
  ): (key: StimmzettelRepoKey) => boolean {
    return (key: StimmzettelRepoKey) => {
      logging.logDebug("createKeyPredicateTeamIdIsEqualTo", key, teamID);
      return key.teamID === teamID;
    };
  }

  async function _sendAndStoreResult(stimmzettel: PersistedStimmzettel) {
    return stimmzettelService
      .saveSingleStimmzettel(wahlID, wahlbezirkID, stimmzettel)
      .then(() =>
        stimmzettelRepo.setByKey(
          _createKey(stimmzettel),
          createIndexedDBItem(stimmzettel, FetchStateEnum.DONE)
        )
      )
      .catch(() =>
        stimmzettelRepo.setByKey(
          _createKey(stimmzettel),
          createIndexedDBItem(stimmzettel, FetchStateEnum.ERROR)
        )
      );
  }

  async function _fetchAndStoreResult(teamID: string) {
    const fetchedStimmzettel = await stimmzettelService.getStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID
    );
    fetchedStimmzettel.forEach((stimmzettel) =>
      stimmzettelRepo.setByKey(
        _createKey(stimmzettel),
        createIndexedDBItem(stimmzettel, FetchStateEnum.DONE)
      )
    );
  }

  async function _replaceStoredStimmzettelOfTeam(teamID: string) {
    await stimmzettelRepo.deleteByKeyPredicate(
      _createKeyPredicateTeamIdIsEqualTo(teamID)
    );
    await _fetchAndStoreResult(teamID);
  }

  return {
    hasAnyDataLoaded: stimmzettelRepo.hadAnyData,
    getStimmzettelOfTeam,
    getTasksToSync,
    loadStimmzettelOfTeam,
    setStimmzettel,
  };
}
