<template>
  <div>
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
  </div>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import BaseBeschlussfassungUebersichtTable from "@/components/dse/beschlussfassung/BaseBeschlussfassungUebersichtTable.vue";
import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";
import { ROUTE_FINISHED } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
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

  await router.push({
    name: ROUTE_FINISHED,
    params: { wahlId: wahlID, wahlbezirkId: wahlbezirkID },
  });
}

function onBeschlussBearbeitenClicked(stimmzettelToEdit: Stimmzettel) {
  // TODO Bearbeiten-Funktionalität Platzhalter. #3270
  console.debug(JSON.stringify(stimmzettelToEdit));
}
</script>
