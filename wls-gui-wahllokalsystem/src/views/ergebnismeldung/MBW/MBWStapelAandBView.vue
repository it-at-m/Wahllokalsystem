<template>
  <the-m-b-w-gueltige-stimmzettel-erfassen-card
    v-if="wahlbezirkID && wahlID"
    :wahlbezirk-i-d="wahlbezirkID"
    :wahl-i-d="wahlID"
    :model-value="ergebnisse"
  />
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";

import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import TheMBWGueltigeStimmzettelErfassenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmzettelErfassenCard.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";

const route = useRoute();

const wahlID = route.params.wahlId as string;
const wahlbezirkID = route.params.wahlbezirkId as string;

const ergebnisse = ref<MbwErgebnisseAndWahlvorschlag[]>([]);

onMounted(async () => {
  if (wahlID && wahlbezirkID) {
    const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
      wahlID,
      wahlbezirkID
    );

    ergebnisse.value = await loadAndCombineErgebnisseAndWahlvorschlaege();
  }
});
</script>
