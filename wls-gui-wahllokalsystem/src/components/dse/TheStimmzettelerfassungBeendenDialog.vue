<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Beenden der Stimmzettelerfassung"
    :is-confirm-loading="isSaving"
    confirmtext="Bestätigen"
    canceltext="Abbrechen"
    icon="$warning"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    Bitte bestätigen Sie, dass die Stimmzettelerfassung in Ihrem Team
    abgeschlossen ist und keine weiteren Papier-Stimmzettel mehr erfasst oder
    korrigiert werden sollen.
  </base-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useStimmzettelerfassungStatusTeamService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { ROUTE_FINISHED } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

const { hasRoleErfassungsteam } = storeToRefs(useUserStore());
const { isSaving, postErfassungTeamStatus } =
  useStimmzettelerfassungStatusTeamService();

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  teamId: string;
}>();

function onCancelClicked(): void {
  closeDialog();
}

async function onConfirmClicked() {
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

function closeDialog() {
  isDialogVisible.value = false;
}
</script>
