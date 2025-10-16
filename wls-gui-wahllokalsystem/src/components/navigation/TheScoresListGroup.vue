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
      :to="routeWithName(ROUTE_WAHLSCHEINE)"
    />
    <v-list-item
      v-if="isUWB"
      title="Stimmabgabevermerke"
      :to="routeWithName(ROUTE_STIMMABGABEVERMERKE)"
    />
    <the-scores-list-group-selector
      v-for="wahl in wahlenState.wahlen"
      :key="wahl.wahlID"
      :wahl="wahl"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheScoresListGroupSelector from "@/components/navigation/TheScoresListGroupSelector.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_STIMMABGABEVERMERKE, ROUTE_WAHLSCHEINE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { isBWB, isUWB } = storeToRefs(useUserStore());
const { routeWithName } = useNavigationUtils();

const { wahlenState } = storeToRefs(useWahlenStore());
</script>
