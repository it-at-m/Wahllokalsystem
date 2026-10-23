import { storeToRefs } from "pinia";
import { onActivated } from "vue";

import { useMonitoringViewBeschlussfassungButtonsUtils } from "@/composables/dse/monitoring/monitoringViewBeschlussfassungButtonsUtils.ts";
import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";
import { useSynchronizeDataResultTools } from "@/composables/indexDB/synchronizeDataResultTools.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import router from "@/plugins/router.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useMonitoringViewUtils(wahlID: string, wahlbezirkID: string) {
  const stimmzettelerfassungState = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const { addNotification } = useUserNotificationService();

  const { synchronizeOfflineData } = useDataSyncStore();
  const { hasRunAndSuccessfullySynchronizedAllDirtyData } =
    useSynchronizeDataResultTools();

  const stimmzettelerfassungTeamState =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);
  const monitoringViewBeschlussfassungButtonProperties =
    useMonitoringViewBeschlussfassungButtonsUtils(
      stimmzettelerfassungTeamState.isTeamStatusListLoading,
      stimmzettelerfassungState.isWorkflowStatusLoading,
      stimmzettelerfassungTeamState.teamstatusList,
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

    if (_canStimmzettelBeChanged()) {
      const syncResult = await synchronizeOfflineData();
      if (!hasRunAndSuccessfullySynchronizedAllDirtyData(syncResult)) {
        addNotification(
          "Synchronisierung der Daten fehlgeschlagen, probieren sie es bitte erneut.",
          UserNotificationCategoryEnum.ERROR
        );

        return;
      }
    }

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

  function _canStimmzettelBeChanged() {
    return (
      stimmzettelerfassungState.workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.SteAbgeschlossen ||
      stimmzettelerfassungState.workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.BeAbgeschlossen
    );
  }

  async function _loadTeamstatusListe() {
    await stimmzettelerfassungTeamState.loadTeamStatusListe(true);
  }

  return {
    onMonitoringSynchronisierenClicked,
    reopenStimmzettelerfassung,

    ...stimmzettelerfassungTeamState,
    ...stimmzettelerfassungState,
    ...monitoringViewBeschlussfassungButtonProperties,
  };
}
