<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Bitte die Vollständigkeit der Stimmzettelerfassung prüfen"
    confirmtext="Weiter zur Beschlussfassung"
    :canceltext="cancelText"
    :icon="
      isStimmzettelCountAndStimmzettelumschlaegeCountEqual
        ? '$information'
        : '$warning'
    "
    :is-confirm-loading="isConfirmButtonInLoadingState"
    :cancel-disabled="isAnzahlStimmzettelLoading"
    :is-confirm-active="false"
    @cancel="cancelDialog"
    @confirm="onConfirmClicked"
  >
    <v-skeleton-loader
      v-if="
        isAnzahlStimmzettelLoading ||
        stimmzettelCount === null ||
        stimmzettelumschlaegeCount === null
      "
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
      <div
        v-else
        class="text-warning"
      >
        Die Anzahl der gezählten Stimmzettel ({{ stimmzettelumschlaegeCount }})
        unterscheidet sich um
        {{ Math.abs(stimmzettelCount - stimmzettelumschlaegeCount) }} von der
        Anzahl der erfassten Stimzettel ({{ stimmzettelCount }})
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
  continueProcessAfterForceRefreshOfForeignStimmzettel,
} = useBeschlussfassungStartenDialogUtils(
  props.wahlId,
  props.wahlbezirkId,
  closeDialog
);

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
    : "Stimmzettelanzahl korrigieren"
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
  await continueProcessAfterForceRefreshOfForeignStimmzettel();
}
</script>
