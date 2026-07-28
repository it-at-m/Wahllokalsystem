<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Beenden der Stimmzettelerfassung"
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
import { ROUTE_FINISHED } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

const { isUWB } = storeToRefs(useUserStore());

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
}>();

function onCancelClicked(): void {
  closeDialog();
}

async function onConfirmClicked() {
  if (isUWB.value) {
    await router.push({
      name: DseStepsEnum.DSE_MONITORING,
      params: { wahlId: props.wahlId, wahlbezirkId: props.wahlbezirkId },
    });
  } else {
    await router.push({ name: ROUTE_FINISHED });
  }
  closeDialog();
}

function closeDialog() {
  isDialogVisible.value = false;
}
</script>
