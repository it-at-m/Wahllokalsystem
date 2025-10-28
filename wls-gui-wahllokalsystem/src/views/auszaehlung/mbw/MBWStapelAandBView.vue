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

import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import TheMBWGueltigeStimmzettelErfassenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmzettelErfassenCard.vue";
import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const route = useRoute();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const wahlID = computed(() => route.params.wahlId as string);

const wahlbezirkID = computed(() =>
  getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID.value)
);

const ergebnisse = ref<MbwErgebnisseAndWahlvorschlag[]>([]);

onMounted(async () => {
  if (wahlID.value && wahlbezirkID.value) {
    const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
      wahlID.value,
      wahlbezirkID.value
    );

    ergebnisse.value = await loadAndCombineErgebnisseAndWahlvorschlaege();
  }
});
</script>
