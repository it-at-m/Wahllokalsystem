<template>
  <v-card>
    <v-card-title>Beschlussfassung</v-card-title>
    <v-card-text>
      <base-progress-linear
        class="text-center"
        titel="bereits gefasste Beschlüsse"
        :is-loading="false"
        :current="completedStimmzettelForBeschlussfassung.length"
        :total="stimmzettelForBeschlussfassung.length"
        color="success" />
      <base-beschlussfassung-uebersicht-table
        :stimmzettel-liste="stimmzettelForBeschlussfassung"
        :stimmzettel-loading="isStimmzettelForBeschlussLoading"
        @edit-beschluss-stimmzettel="onBeschlussBearbeitenClicked($event)"
    /></v-card-text>
    <v-card-actions>
      <v-spacer />
      <base-text-button
        active
        :disabled="isBeschlussfassungBeendenButtonDisabled"
        @click="onBeschlussfassungBeendenClicked"
      >
        Beschlussfassung Beenden
      </base-text-button>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import BaseBeschlussfassungUebersichtTable from "@/components/dse/beschlussfassung/BaseBeschlussfassungUebersichtTable.vue";
import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import router from "@/plugins/router.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
const { setStepDone } = useWorkflowStore();
const { getNextRoute } = useNavigationService();
const route = useRoute();

const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const {
  isStimmzettelForBeschlussLoading,
  stimmzettelForBeschlussfassung,
  isBeschlussfassungBeendenButtonDisabled,
  completedStimmzettelForBeschlussfassung,
} = useBeschlussfassungViewUtils(wahlID, wahlbezirkID);

async function onBeschlussfassungBeendenClicked() {
  await saveDseWorkflowStatus(wahlID, wahlbezirkID, {
    status: StimmzettelerfassungStatusEnum.BeAbgeschlossen,
  });

  setStepDone(wahlID, wahlbezirkID, MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG);

  await router.push(getNextRoute());
}

function onBeschlussBearbeitenClicked(stimmzettelToEdit: PersistedStimmzettel) {
  // TODO Bearbeiten-Funktionalität Platzhalter. #3270
  console.debug(JSON.stringify(stimmzettelToEdit));
}
</script>
