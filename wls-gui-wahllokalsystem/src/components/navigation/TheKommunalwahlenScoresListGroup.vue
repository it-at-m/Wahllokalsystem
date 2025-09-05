<template>
  <v-list-group value="Ergebnisse">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Ergebnisermittlung"
      />
    </template>
    <v-list-item
      v-if="isBWB"
      title="Wahlscheine"
      to="/Wahlscheine"
    />
    <v-list-item
      v-if="isUWB"
      title="Stimmabgabevermerke"
      :to="routeWithName(ROUTE_STIMMABGABEVERMERKE)"
    />
    <the-o-b-w-scores-list-group :title-stimmen-zaehlen="titleStimmenZaehlen" />
    <the-s-r-w-scores-list-group :title-stimmen-zaehlen="titleStimmenZaehlen" />
    <the-b-a-w-scores-list-group :title-stimmen-zaehlen="titleStimmenZaehlen" />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheBAWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheBAWScoresListGroup.vue";
import TheOBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheOBWScoresListGroup.vue";
import TheSRWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheSRWScoresListGroup.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_STIMMABGABEVERMERKE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { isBWB, isUWB } = storeToRefs(useUserStore());
const { routeWithName } = useNavigationUtils();

const titleStimmenZaehlen = computed(() => {
  return isBWB.value
    ? "Zählen der Stimmzettelumschläge"
    : "Zählen der Stimmzettel";
});
</script>
