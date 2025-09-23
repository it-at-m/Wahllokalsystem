<template>
  <div v-if="wahlbezirkID && wahlID">
    <the-o-b-w-stapel-c-erfassung-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-o-b-w-stapel-c-summary-card
      v-if="totalSum > 0"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";

import TheOBWStapelCErfassungCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCErfassungCard.vue";
import TheOBWStapelCSummaryCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCSummaryCard.vue";
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { params: routeParams } = useRoute();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();

const wahlID = computed(() => routeParams.wahlId as string);
const wahlbezirkID = computed(
  () => getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID.value) ?? ""
);

const { totalSum } = useOBWStapelCUtils(wahlID, wahlbezirkID);
</script>
