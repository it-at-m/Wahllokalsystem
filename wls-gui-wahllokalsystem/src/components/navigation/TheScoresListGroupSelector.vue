<template>
  <the-o-b-w-scores-list-group
    v-if="wahl.wahlart === WahlWahlartEnum.Obw"
    :title-stimmen-zaehlen="titleStimmenZaehlen"
  />
  <the-s-r-w-scores-list-group
    v-if="wahl.wahlart === WahlWahlartEnum.Srw"
    :title-stimmen-zaehlen="titleStimmenZaehlen"
  />
  <the-b-a-w-scores-list-group
    v-if="wahl.wahlart === WahlWahlartEnum.Baw"
    :title-stimmen-zaehlen="titleStimmenZaehlen"
  />
</template>

<script setup lang="ts">
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheBAWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheBAWScoresListGroup.vue";
import TheOBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheOBWScoresListGroup.vue";
import TheSRWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheSRWScoresListGroup.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

defineProps({
  wahl: {
    type: Object as PropType<Wahl>,
    required: true,
  },
});

const { isBWB } = storeToRefs(useUserStore());

const titleStimmenZaehlen = computed(() => {
  return isBWB.value
    ? "Zählen der Stimmzettelumschläge"
    : "Zählen der Stimmzettel";
});
</script>

<style scoped></style>
