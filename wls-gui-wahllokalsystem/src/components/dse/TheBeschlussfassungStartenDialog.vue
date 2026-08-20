<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Bitte die Vollständigkeit der Stimmzettelerfassung prüfen"
    confirmtext="Weiter zur Beschlussfassung"
    canceltext="Beschlussfassung nicht starten"
    icon="$information"
    :is-confirm-loading="isConfirmButtonInLoadingState"
    :cancel-disabled="isLoadingAnzahlStimmzettel"
    @cancel="closeDialog"
    @confirm="onConfirmClicked"
  >
    <v-skeleton-loader
      v-if="isLoadingAnzahlStimmzettel || lastLoadedAnzahlStimmzettel === null"
      type="text"
    />
    <div v-else>
      Bitte bestätigen Sie, dass alle Papier-Stimmzettel aus dem gemeinsamen
      Erfassungsvorrat korrekt erfasst wurden. Es wurden insgesamt
      {{ lastLoadedAnzahlStimmzettel }} Stimmzettel von
      {{ teamstatusList.length }}
      {{ teamstatusList.length == 1 ? "Team" : "Teams" }} im System erfasst.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

import { watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useTheBeschlussfassungStartenDialogUtils } from "@/composables/dse/theBeschlussfassungStartenDialog.ts";

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  teamstatusList: StimmzettelerfassungTeamStatusEntry[];
}>();

const {
  isLoadingAnzahlStimmzettel,
  isConfirmButtonInLoadingState,
  lastLoadedAnzahlStimmzettel,
  loadAnzahlStimmzettel,
  updateWorkflowStatusAndNavigate,
} = useTheBeschlussfassungStartenDialogUtils();

watch(
  () => isDialogVisible.value,
  async (newVal) => {
    if (newVal) {
      await loadAnzahlStimmzettel(props.wahlId, props.wahlbezirkId);
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
