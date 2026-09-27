import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";
import type { Ref } from "vue";

import { computed, onActivated, readonly, ref } from "vue";

import { useStimmzettelErfassungViewButtonStateUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewButtonStateUtils.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelErfassungTeamStatusService.ts";
import { useWithInProgress } from "@/composables/experimental/indexedDBV2/inProgress.ts";

const { getEmptyStimmzettelWithStimmzettelkennung } = useStimmzettelTools();
const { createWrappedFunction } = useWithInProgress();

export function useStimmzettelErfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const statusService = useStimmzettelerfassungTeamStatusService(
    wahlID,
    wahlbezirkID
  );
  const wrappedGetTeamStatus = createWrappedFunction(
    statusService.getTeamStatus
  );
  const wrappedRefreshTeamStatus = createWrappedFunction(
    statusService.refreshTeamStatus
  );

  const teamStatus = ref<StimmzettelerfassungTeamStatus | null>(null);
  const isStatusLoading = computed(
    () =>
      wrappedGetTeamStatus.isInProgress.value ||
      wrappedRefreshTeamStatus.isInProgress.value
  );
  const activeStimmzettel: Ref<PersistedStimmzettel | null> = ref(null);

  //DialogVisibilityState
  const isKennungsDialogVisible = ref(false);
  const isErfassungsDialogVisible = ref(false);

  const buttonUtils = useStimmzettelErfassungViewButtonStateUtils(teamStatus);

  //Hooks
  onActivated(async () => {
    teamStatus.value = await wrappedGetTeamStatus.run(teamID);
  });

  //Public functions
  function startNewEmptyStimmzettelWithStimmzettelkennung(
    stimmzettelkennung: number
  ) {
    activeStimmzettel.value =
      getEmptyStimmzettelWithStimmzettelkennung(stimmzettelkennung);
  }

  async function setStatusInBearbeitung() {
    await statusService.setStatusInBearbeitung(teamID);
    teamStatus.value = await wrappedGetTeamStatus.run(teamID);
  }

  async function sendStatusUnterbrochen() {
    await statusService.setStatusUnterbrochen(teamID);
    teamStatus.value = await wrappedGetTeamStatus.run(teamID);
  }

  async function reloadTeamStatus() {
    const refreshedTeamStatus = await wrappedRefreshTeamStatus.run(teamID);
    teamStatus.value = refreshedTeamStatus.newValue;
  }

  return {
    //Props
    activeStimmzettel,
    teamStatus: readonly(teamStatus),
    isErfassungsDialogVisible,
    isKennungsDialogVisible,
    isStatusLoading: readonly(isStatusLoading),

    //actions
    setStatusInBearbeitung,
    sendStatusUnterbrochen,
    startNewEmptyStimmzettelWithStimmzettelkennung,
    reloadTeamStatus,

    //imported functions
    ...buttonUtils,
  };
}
