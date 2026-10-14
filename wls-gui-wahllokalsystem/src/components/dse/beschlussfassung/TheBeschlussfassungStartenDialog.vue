<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Bitte die Vollständigkeit der Stimmzettelerfassung prüfen"
    confirmtext="Weiter zur Beschlussfassung"
    :canceltext="cancelText"
    icon="$information"
    :is-confirm-loading="isConfirmButtonInLoadingState"
    :cancel-disabled="isAnzahlStimmzettelLoading"
    @cancel="cancelDialog"
    @confirm="onConfirmClicked"
  >
    <v-skeleton-loader
      v-if="isAnzahlStimmzettelLoading || stimmzettelCount === null"
      type="text"
    />
    <div v-else>
      <div>
        Es wurden insgesamt
        {{ stimmzettelCount }} Stimmzettel von {{ teamstatusList.length }}
        {{ teamstatusList.length == 1 ? "Team" : "Teams" }} im System erfasst.
      </div>
      <div v-if="isStimmzettelCountAndStimmzettelumschlaegeCountEqual">
        Bitte bestätigen Sie, dass alle Papier-Stimmzettel aus dem gemeinsamen
        Erfassungsvorrat korrekt erfasst wurden.
      </div>
      <div v-else>
        Als Anzahl der Stimmzettel wurde vor der Stimmzettelerfassung jedoch
        {{ stimmzettelumschlaegeCount }} angegeben.
      </div>
    </div>
  </base-dialog>
</template>

<script setup lang="ts">
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { computed, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassung/beschlussfassungStartenDialogUtils.ts";
import router from "@/plugins/router.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

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
  isAnzahlStimmzettelLoading,
  isConfirmButtonInLoadingState,
  stimmzettelCount,
  stimmzettelumschlaegeCount,
  loadAnzahlStimmzettel,
  updateWorkflowStatusAndNavigate,
} = useBeschlussfassungStartenDialogUtils();

watch(
  () => isDialogVisible.value,
  async (newVal) => {
    if (newVal) {
      await loadAnzahlStimmzettel(props.wahlId, props.wahlbezirkId);
    }
  }
);

const isStimmzettelCountAndStimmzettelumschlaegeCountEqual = computed(
  () => stimmzettelCount.value === stimmzettelumschlaegeCount.value
);

const cancelText = computed(() =>
  isStimmzettelCountAndStimmzettelumschlaegeCountEqual.value
    ? "Beschlussfassung nicht starten"
    : "Zur Erfassung der Stimmzettelanzahl"
);

function closeDialog() {
  isDialogVisible.value = false;
}

async function cancelDialog() {
  if (!isStimmzettelCountAndStimmzettelumschlaegeCountEqual.value) {
    await router.push({
      name: MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
      params: { wahlId: props.wahlId, wahlbezirkId: props.wahlbezirkId },
    });
  }
  closeDialog();
}

async function onConfirmClicked() {
  await updateWorkflowStatusAndNavigate(props.wahlId, props.wahlbezirkId);
  closeDialog();
}
</script>
