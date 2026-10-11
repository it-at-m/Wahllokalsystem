import { storeToRefs } from "pinia";
import { onActivated } from "vue";

import { useMonitoringViewBeschlussfassungButtonsUtils } from "@/composables/dse/monitoring/monitoringViewBeschlussfassungButtonsUtils.ts";
import { useStimmzettelerfassungTeamStatusDataService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelErfassungTeamStatusDataService.ts";
import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";
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
  const stimmzettelerfassungTeamListState =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);
  const { reopenStimmzettelerfassung: reopenErfassungForTeam } =
    useStimmzettelerfassungTeamStatusDataService(wahlID, wahlbezirkID);

  const monitoringViewBeschlussfassungButtonProperties =
    useMonitoringViewBeschlussfassungButtonsUtils(
      stimmzettelerfassungTeamListState.isTeamStatusListLoading,
      stimmzettelerfassungState.isWorkflowStatusLoading,
      stimmzettelerfassungTeamListState.teamstatusList,
      stimmzettelerfassungState.workflowStatus,
      wahlID,
      wahlbezirkID
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

    await reopenErfassungForTeam(teamID);

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
    await stimmzettelerfassungTeamListState.loadTeamStatusListe();
  }

  return {
    onMonitoringSynchronisierenClicked,
    reopenStimmzettelerfassung,

    ...stimmzettelerfassungTeamListState,
    ...stimmzettelerfassungState,
    ...monitoringViewBeschlussfassungButtonProperties,
  };
}
