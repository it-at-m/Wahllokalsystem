<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Beenden der Stimmzettelerfassung"
    :is-confirm-loading="isSaving || isOfflineDataSyncing"
    confirmtext="Synchronisieren und Bestätigen"
    canceltext="Abbrechen"
    icon="$warning"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    <div>
      Bitte bestätigen Sie, dass die Stimmzettelerfassung in Ihrem Team
      abgeschlossen ist und keine weiteren Papier-Stimmzettel mehr erfasst oder
      korrigiert werden sollen.
    </div>
    <v-card v-if="isSyncWidgetVisible">
      <v-card-title>Offline-Synchronisierung</v-card-title>
      <v-card-text>
        <the-offline-data-sync-widget />
      </v-card-text>
    </v-card>
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import TheOfflineDataSyncWidget from "@/components/common/widgets/TheOfflineDataSyncWidget.vue";
import { useStimmzettelerfassungStatusTeamService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ROUTE_FINISHED } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { hasRoleErfassungsteam } = storeToRefs(useUserStore());
const { isSaving, postErfassungTeamStatus } =
  useStimmzettelerfassungStatusTeamService();

const { addNotification } = useUserNotificationService();

const dataSyncStore = useDataSyncStore();
const { synchronizeOfflineData } = dataSyncStore;
const { isOfflineDataSyncing, numberOfDirtyTasksAfterSync } =
  storeToRefs(dataSyncStore);

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  teamId: string;
}>();

const isSyncWidgetVisible = ref(false);

function onCancelClicked(): void {
  closeDialog();
}

async function onConfirmClicked() {
  isSyncWidgetVisible.value = true;
  await synchronizeOfflineData();
  if (numberOfDirtyTasksAfterSync.value) {
    addNotification(
      "Beenden kann nicht abgeschlossen werden, weil die Synchronisierung nicht erfolgreich war.",
      UserNotificationCategoryEnum.ERROR
    );
  } else {
    await postErfassungTeamStatus(
      props.wahlId,
      props.wahlbezirkId,
      props.teamId,
      { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
      true
    );

    if (hasRoleErfassungsteam.value) {
      await router.push({ name: ROUTE_FINISHED });
    } else {
      await router.push({
        name: DseStepsEnum.DSE_MONITORING,
        params: { wahlId: props.wahlId, wahlbezirkId: props.wahlbezirkId },
      });
    }
    closeDialog();
  }
}

function closeDialog() {
  isDialogVisible.value = false;
  isSyncWidgetVisible.value = false;
}
</script>
