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
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { params: routeParams } = useRoute();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { sm, name } = useDisplay();

const wahlID = computed(() => routeParams.wahlId as string);
const wahlbezirkID = computed(
  () => getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID.value) ?? ""
);

const { totalSum } = useOBWStapelCUtils(wahlID, wahlbezirkID);

const colsErfassungsCard = computed(() =>
  hasResultsToShow.value ? (sm.value ? 12 : 8) : 12
);
const colsSummaryCard = computed(() => (sm.value ? 12 : 4));
const hasResultsToShow = computed(() => totalSum.value > 0);
</script>
