<template>
  <v-row v-if="wahlbezirkID && wahlID">
    <v-col :cols="colsErfassungsCard">
      <the-o-b-w-stapel-c-erfassung-card
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
    </v-col>
    <v-col
      v-if="hasResultsToShow"
      :cols="colsSummaryCard"
    >
      <the-o-b-w-stapel-c-summary-card
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import { useDisplay } from "vuetify/framework";

import TheOBWStapelCErfassungCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCErfassungCard.vue";
import TheOBWStapelCSummaryCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCSummaryCard.vue";
import { useOBWStapelCUtils } from "@/composables/ergebnismeldung/OBW/obwStapelCUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { params: routeParams } = useRoute();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { smAndDown } = useDisplay();

const wahlID = routeParams.wahlId as string;
const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID) ?? "";

const { wahlvorschlaegeAndSumAboveZero, stapelCUngueltigErgebnisseSum } =
  useOBWStapelCUtils(
    computed(() => wahlID),
    computed(() => wahlbezirkID)
  );

const colsErfassungsCard = computed(() =>
  hasResultsToShow.value ? (smAndDown.value ? 12 : 8) : 12
);
const colsSummaryCard = computed(() => (smAndDown.value ? 12 : 4));
const hasResultsToShow = computed(
  () =>
    wahlvorschlaegeAndSumAboveZero.value.length > 0 ||
    stapelCUngueltigErgebnisseSum.value > 0
);
</script>
