<template>
  <base-dialog
    v-if="hasRoleSchriftfuehrung"
    :visible="isDialogVisible"
    dialogtitle="Bitte die Vollständigkeit der Stimmzettelerfassung prüfen"
    confirmtext="Weiter zur Beschlussfassung"
    canceltext="Beschlussfassung nicht starten"
    icon="$information"
    @cancel="closeDialog"
    @confirm="onConfirmClicked"
  >
    <div>
      Bitte bestätigen Sie, dass alle Papier-Stimmzettel aus dem gemeinsamen
      Erfassungsvorrat korrekt erfasst wurden. Es wurden insgesamt
      {{ stimmzettelCount }} Stimmzettel von {{ teamstatusList.length }}
      {{ teamstatusList.length == 1 ? "Team" : "Teams" }} im System erfasst.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassungStartenDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  teamstatusList: StimmzettelerfassungTeamStatusEntry[];
}>();

const stimmzettelCount = ref(0);

const { loadStimmzettelCount, updateWorkflowStatusAndNavigate } =
  useBeschlussfassungStartenDialogUtils();
const { hasRoleSchriftfuehrung } = storeToRefs(useUserStore());

watch(
  () => isDialogVisible.value,
  async (newVal) => {
    if (newVal) {
      stimmzettelCount.value = await loadStimmzettelCount(
        props.wahlId,
        props.wahlbezirkId,
        props.teamstatusList
      );
    }
  }
);

function closeDialog() {
  isDialogVisible.value = false;
}

async function onConfirmClicked() {
  await updateWorkflowStatusAndNavigate(props.wahlId, props.wahlbezirkId);
  closeDialog();
}
</script>
