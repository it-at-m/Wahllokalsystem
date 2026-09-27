import { storeToRefs } from "pinia";
import { onActivated } from "vue";

import { useMonitoringViewBeschlussfassungButtonsUtils } from "@/composables/dse/monitoring/monitoringViewBeschlussfassungButtonsUtils.ts";
import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import router from "@/plugins/router.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

export function useMonitoringViewUtils(wahlID: string, wahlbezirkID: string) {
  const stimmzettelerfassungState = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const stimmzettelerfassungTeamState =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);
  const monitoringViewBeschlussfassungButtonProperties =
    useMonitoringViewBeschlussfassungButtonsUtils(
      stimmzettelerfassungTeamState.isTeamStatusListLoading,
      stimmzettelerfassungState.isWorkflowStatusLoading,
      stimmzettelerfassungTeamState.teamstatusList,
      stimmzettelerfassungState.workflowStatus
    );

  async function onMonitoringSynchronisierenClicked() {
    await _loadTeamstatusListe();
  }

  onActivated(async () => {
    await Promise.allSettled([
      _loadTeamstatusListe(),
      stimmzettelerfassungState.loadWorkflowStatus(),
    ]);
  });

  async function reopenStimmzettelerfassung(teamID: string) {
    const { setStepDone } = useWorkflowStore();
    const { currentUserTeamName } = storeToRefs(useUserStore());
    const { getNextRoute } = useNavigationService();

    await useStimmzettelerfassungTeamStatusService().reopenStimmzettelerfassung(
      wahlID,
      wahlbezirkID,
      teamID,
      true
    );

    setStepDone(
      wahlID,
      wahlbezirkID,
      MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS,
      false
    );
    setStepDone(
      wahlID,
      wahlbezirkID,
      MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
      false
    );

    //The current team (=> Schriftfuehrung)
    if (currentUserTeamName.value == teamID) {
      setStepDone(
        wahlID,
        wahlbezirkID,
        MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
        false
      );
      //Return to Stimmzettelerfassung
      await router.push(getNextRoute());
    }
    //Another Team (=> Erfassungsteam)
    else {
      await _loadTeamstatusListe();
    }
  }

  async function _loadTeamstatusListe() {
    await stimmzettelerfassungTeamState.loadTeamStatusListe();
  }

  return {
    onMonitoringSynchronisierenClicked,
    reopenStimmzettelerfassung,

    ...stimmzettelerfassungTeamState,
    ...stimmzettelerfassungState,
    ...monitoringViewBeschlussfassungButtonProperties,
  };
}
