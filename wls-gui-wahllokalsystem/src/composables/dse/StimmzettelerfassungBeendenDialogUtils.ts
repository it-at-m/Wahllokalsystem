import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ROUTE_FINISHED } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelerfassungBeendenDialogUtils(
  wahlId: string,
  wahlbezirkId: string,
  closeDialogCallback: () => void
) {
  const { addNotification } = useUserNotificationService();
  const { synchronizeOfflineData } = useDataSyncStore();
  const { hasRoleErfassungsteam, currentUserTeamName } =
    storeToRefs(useUserStore());
  const { isSaving, postErfassungTeamStatus } =
    useStimmzettelerfassungTeamStatusService();

  const isSyncWidgetVisible = ref(false);

  async function synchronizeDataAndPostTeamErfassungDone() {
    isSyncWidgetVisible.value = true;
    const syncResult = await synchronizeOfflineData();

    if (!syncResult) {
      addNotification(
        "Synchronising läuft bereits. Bitte versuchen Sie es erneut später.",
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
    if (hasRoleErfassungsteam.value) {
      await router.push({ name: ROUTE_FINISHED });
    } else {
      await router.push({
        name: DseStepsEnum.DSE_MONITORING,
        params: { wahlId: wahlId, wahlbezirkId: wahlbezirkId },
      });
    }
  }

  return {
    isSyncWidgetVisible,
    isSaving,

    synchronizeDataAndPostTeamErfassungDone,
  };
}
