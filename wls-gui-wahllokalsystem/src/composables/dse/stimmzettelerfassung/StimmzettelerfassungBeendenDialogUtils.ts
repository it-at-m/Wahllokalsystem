import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import router from "@/plugins/router.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelerfassungBeendenDialogUtils(
  wahlId: string,
  wahlbezirkId: string,
  closeDialogCallback: () => void
) {
  const { addNotification } = useUserNotificationService();
  const navigationService = useNavigationService();
  const { setStepDone } = useWorkflowStore();
  const { synchronizeOfflineData } = useDataSyncStore();
  const { currentUserTeamName } = storeToRefs(useUserStore());
  const { isSaving, postErfassungTeamStatus } =
    useStimmzettelerfassungTeamStatusService();

  const isSyncWidgetVisible = ref(false);

  async function synchronizeDataAndPostTeamErfassungDone() {
    isSyncWidgetVisible.value = true;
    const syncResult = await synchronizeOfflineData();

    if (!syncResult) {
      addNotification(
        "Synchronisierung läuft bereits. Bitte versuchen Sie es später erneut.",
        UserNotificationCategoryEnum.WARNING
      );
      return;
    }

    if (syncResult.numberOfDirtyTasksRemaining > 0) {
      addNotification(
        "Beenden kann nicht abgeschlossen werden, weil die Synchronisierung nicht erfolgreich war.",
        UserNotificationCategoryEnum.ERROR
      );
      return;
    }
    await postErfassungTeamStatus(
      wahlId,
      wahlbezirkId,
      currentUserTeamName.value,
      { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
      true
    );

    await _navigateToNextView();

    closeDialogCallback();
  }

  async function _navigateToNextView() {
    setStepDone(
      wahlId,
      wahlbezirkId,
      MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG
    );

    await router.push(navigationService.getNextRoute());
  }

  return {
    isSyncWidgetVisible,
    isSaving,

    synchronizeDataAndPostTeamErfassungDone,
  };
}
