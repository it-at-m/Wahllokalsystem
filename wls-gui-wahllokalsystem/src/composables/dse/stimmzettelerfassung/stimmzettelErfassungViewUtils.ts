import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import { onActivated, readonly, ref } from "vue";

import { useStimmzettelErfassungViewButtonStateUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewButtonStateUtils.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();

export function useStimmzettelErfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string,
  forceRefreshStimmzettel: () => Promise<void>
) {
  const teamStatus = ref<StimmzettelerfassungTeamStatus | null>(null);
  const isStatusLoading = ref(false);
  const statusBeingWritten = ref<StimmzettelerfassungTeamStatusEnum | null>(
    null
  );

  //DialogVisibilityState
  const isKennungsDialogVisible = ref(false);

  const buttonUtils = useStimmzettelErfassungViewButtonStateUtils(teamStatus);

  //Hooks
  onActivated(async () => {
    await _loadTeamStatus(false);
  });

  //Public functions
  async function ensureStatusInBearbeitung(sendNotification = false) {
    if (
      teamStatus.value?.status !==
      StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
    ) {
      await _postTeamStatus(
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
        sendNotification
      );
    }
  }

  async function sendStatusUnterbrochen(sendNotification = false) {
    await _postTeamStatus(
      StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
      sendNotification
    );
  }

  async function reloadTeamStatus() {
    await _loadTeamStatus(false);
    const lastOfflineFirstTeamStatus = teamStatus.value;
    await _loadTeamStatus(true);
    const currentActiveTeamStatus = teamStatus.value;

    if (
      _hasTeamStatusSwitchedFromAbgeschlossenToInBearbeitung(
        lastOfflineFirstTeamStatus,
        currentActiveTeamStatus
      )
    ) {
      await forceRefreshStimmzettel();
    }
  }

  //private functions
  function _hasTeamStatusSwitchedFromAbgeschlossenToInBearbeitung(
    lastOfflineFirstTeamStatus: StimmzettelerfassungTeamStatus | null,
    currentActiveTeamStatus: StimmzettelerfassungTeamStatus | null
  ) {
    return (
      lastOfflineFirstTeamStatus?.status ===
        StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN &&
      currentActiveTeamStatus?.status ===
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
    );
  }

  async function _loadTeamStatus(forceRefresh: boolean) {
    isStatusLoading.value = true;
    try {
      const loaded = await erfassungTeamStatusService.loadErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        false,
        forceRefresh
      );
      if (loaded) {
        teamStatus.value = loaded;
      }
    } finally {
      isStatusLoading.value = false;
    }
  }

  async function _postTeamStatus(
    statusToChange: StimmzettelerfassungTeamStatusEnum,
    sendNotification: boolean
  ) {
    const newStatus: StimmzettelerfassungTeamStatus = {
      status: statusToChange,
    };
    statusBeingWritten.value = statusToChange;
    try {
      await erfassungTeamStatusService.postErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        newStatus,
        sendNotification
      );
      teamStatus.value = newStatus;
    } finally {
      statusBeingWritten.value = null;
    }
  }

  return {
    //Props
    teamStatus: readonly(teamStatus),
    isKennungsDialogVisible,
    isStatusLoading: readonly(isStatusLoading),
    statusBeingWritten: readonly(statusBeingWritten),

    //actions
    ensureStatusInBearbeitung,
    sendStatusUnterbrochen,
    reloadTeamStatus,

    //imported functions
    ...buttonUtils,
  };
}
