import type { SyncTask } from "@/composables/experimental/experimentalStimmzettelService.ts";
import type { ChangedIndexDBItem } from "@/composables/experimental/indexedDBV2/shared/changedIndexDBItem.ts";
import type { DataProviderGetOptions } from "@/composables/experimental/indexedDBV2/shared/DataProviderGetOptions.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import { useStimmzettelerfassungTeamStatusFetchService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusFetchService.ts";
import { useTeamStatusRepo } from "@/composables/experimental/indexedDBV2/repos/teamStatusRepo.ts";
import { getDefaultDataProviderGetOptions } from "@/composables/experimental/indexedDBV2/shared/DataProviderGetOptions.ts";
import { FetchStateEnum } from "@/composables/experimental/indexedDBV2/shared/fetchStateEnum.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { useStimmzettelerfassungTeamStatusTools } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelerfassungTeamStatusDataService(
  wahlID: string,
  wahlbezirkID: string
) {
  const auszaehlungRepo = useTeamStatusRepo(wahlID, wahlbezirkID);

  const { addNotification } = useUserNotificationService();
  const fetchService = useStimmzettelerfassungTeamStatusFetchService();
  const {
    createAbgeschlossen,
    createInBearbeitung,
    createUnterbrochen,
    createRegistriert,
  } = useStimmzettelerfassungTeamStatusTools();

  async function refreshTeamStatus(
    teamID: string
  ): Promise<ChangedIndexDBItem<StimmzettelerfassungTeamStatus>> {
    const teamStatus = await fetchService.loadErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      false
    );
    const replacedValue = await auszaehlungRepo.replaceTeamStatus(
      teamID,
      teamStatus,
      FetchStateEnum.DONE
    );
    return {
      newValue: teamStatus,
      oldValue: replacedValue?.item ?? null,
    };
  }

  async function getTasksToSync(): Promise<SyncTask[]> {
    const dirtyTeamStatusRequests =
      await auszaehlungRepo.getDirtyTeamStatusRequests();
    return dirtyTeamStatusRequests
      .filter((item) => item.item !== null)
      .map((item) => ({
        name: `Teamstatus ${item.key}`,
        callback: () => _sendAndStoreResult(item.key, item.item),
      }));
  }

  async function getTeamStatus(
    teamID: string,
    options?: Partial<DataProviderGetOptions>
  ): Promise<StimmzettelerfassungTeamStatus | null> {
    const defaultOptions = getDefaultDataProviderGetOptions();
    const mergedOptions = { ...defaultOptions, ...options };

    if (mergedOptions.forceRefresh) {
      await _fetchAndStoreResult(teamID);
    }

    const storedItem = await auszaehlungRepo.getTeamStatus(teamID);
    return storedItem?.item ?? null;
  }

  async function setStatusAbgeschlossen(teamID: string) {
    const abgeschlossenStatus = createAbgeschlossen();
    await fetchService.postErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      abgeschlossenStatus,
      true
    );
    await auszaehlungRepo.setTeamStatus(
      teamID,
      abgeschlossenStatus,
      FetchStateEnum.DONE
    );
  }

  async function setStatusInBearbeitung(teamID: string) {
    const inBearbeitungStatus = createInBearbeitung();
    await _setStatusOfflineFirst(teamID, inBearbeitungStatus);
  }

  async function setStatusUnterbrochen(teamID: string) {
    const unterbrochenStatus = createUnterbrochen();
    await _setStatusOfflineFirst(teamID, unterbrochenStatus);
  }

  async function initTeamStatus(teamID: string) {
    try {
      const teamStatus = await getTeamStatus(teamID, { forceRefresh: true });

      if (!teamStatus) {
        const registeredStatus = createRegistriert();
        await fetchService.postErfassungTeamStatus(
          wahlID,
          wahlbezirkID,
          teamID,
          registeredStatus,
          false
        );
        await auszaehlungRepo.setTeamStatus(
          teamID,
          registeredStatus,
          FetchStateEnum.DONE
        );
      } else if (
        StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN == teamStatus.status
      ) {
        useWorkflowStore().setStepDone(
          wahlID,
          wahlbezirkID,
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG
        );
      }
    } catch (error) {
      addNotification(
        "Teamstatus konnte nicht initialisiert werden.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function _setStatusOfflineFirst(
    teamID: string,
    status: StimmzettelerfassungTeamStatus
  ) {
    await auszaehlungRepo.setTeamStatus(teamID, status, FetchStateEnum.PENDING);

    _sendAndStoreResult(teamID, status);
  }

  async function _fetchAndStoreResult(teamID: string) {
    const teamStatus = await fetchService.loadErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      false
    );
    await auszaehlungRepo.setTeamStatus(
      teamID,
      teamStatus,
      FetchStateEnum.DONE
    );
  }

  async function _sendAndStoreResult(
    teamID: string,
    status: StimmzettelerfassungTeamStatus
  ) {
    return fetchService
      .postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, status, false)
      .then(() =>
        auszaehlungRepo.setTeamStatus(teamID, status, FetchStateEnum.DONE)
      )
      .catch(() =>
        auszaehlungRepo.setTeamStatus(teamID, status, FetchStateEnum.ERROR)
      );
  }

  return {
    initTeamStatus,
    refreshTeamStatus,
    getTasksToSync,
    getTeamStatus,
    setStatusAbgeschlossen,
    setStatusInBearbeitung,
    setStatusUnterbrochen,
  };
}
