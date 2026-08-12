<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Bitte die Vollständigkeit der Stimmzettelerfassung prüfen"
    confirmtext="Weiter zur Beschlussfassung"
    canceltext="Beschlussfassung nicht starten"
    icon="$information"
    :is-confirm-loading="isAnzahlStimmzettelLoading"
    :cancel-disabled="isAnzahlStimmzettelLoading"
    @cancel="closeDialog"
    @confirm="onConfirmClicked"
  >
    <v-skeleton-loader
      v-if="isAnzahlStimmzettelLoading"
      type="text"
    />
    <div v-else>
      Bitte bestätigen Sie, dass alle Papier-Stimmzettel aus dem gemeinsamen
      Erfassungsvorrat korrekt erfasst wurden. Es wurden insgesamt
      {{ stimmzettelCount }} Stimmzettel von {{ teamstatusList.length }}
      {{ teamstatusList.length == 1 ? "Team" : "Teams" }} im System erfasst.
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

import { ref, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassungStartenDialogUtils.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  teamstatusList: StimmzettelerfassungTeamStatusEntry[];
}>();

const isAnzahlStimmzettelLoading = ref(false);
const stimmzettelCount = ref<number | null>(0);

const { updateWorkflowStatusAndNavigate } =
  useBeschlussfassungStartenDialogUtils();
const { getAnzahlStimmzettel } = useStimmzettelService();

watch(
  () => isDialogVisible.value,
  async (newVal) => {
    if (newVal) {
      isAnzahlStimmzettelLoading.value = true;
      stimmzettelCount.value =
        (await getAnzahlStimmzettel(props.wahlId, props.wahlbezirkId)) ?? 0;
      isAnzahlStimmzettelLoading.value = false;
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
