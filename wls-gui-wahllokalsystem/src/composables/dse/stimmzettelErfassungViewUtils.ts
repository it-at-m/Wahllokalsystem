import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { onActivated, readonly, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useStimmzettelErfassungViewButtonStateUtils } from "@/composables/dse/stimmzettelErfassungViewButtonStateUtils.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();
const { logError } = useLogging("stimmzettelErfassungViewUtils");

export function useStimmzettelErfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const teamStatus = ref<StimmzettelerfassungTeamStatus | null>(null);
  const isStatusLoading = ref(false);

  //DialogVisibilityState
  const isKennungsDialogVisible = ref(false);
  const isBeendenDialogVisible = ref(false);
  const isErfassungsDialogVisible = ref(false);

  const buttonUtils = useStimmzettelErfassungViewButtonStateUtils(teamStatus);

  onActivated(async () => {
    await _loadTeamStatus();
  });

  async function sendStatusInBearbeitung() {
    await _postTeamStatus(StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG);
  }

  async function sendStatusUnterbrochen() {
    await _postTeamStatus(StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN);
  }

  async function _loadTeamStatus() {
    isStatusLoading.value = true;
    const loaded = await erfassungTeamStatusService.loadErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      false
    );
    if (loaded) {
      teamStatus.value = loaded;
    }
    isStatusLoading.value = false;
  }

  async function _postTeamStatus(
    statusToChange: StimmzettelerfassungTeamStatusEnum
  ) {
    const newStatus: StimmzettelerfassungTeamStatus = {
      status: statusToChange,
    };
    try {
      await erfassungTeamStatusService.postErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        newStatus,
        false
      );
      teamStatus.value = newStatus;
    } catch (error) {
      logError("Fehler beim Speichern des Team-Status", error);
    }
  }

  return {
    //Props
    teamStatus: readonly(teamStatus),
    isBeendenDialogVisible,
    isErfassungsDialogVisible,
    isKennungsDialogVisible,
    isStatusLoading: readonly(isStatusLoading),

    //actions
    sendStatusInBearbeitung,
    sendStatusUnterbrochen,

    //imported functions
    ...buttonUtils,
  };
}
