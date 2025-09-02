<template>
  <v-list-group value="Ergebnisse">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Ergebnisermittlung"
      />
    </template>
    <v-list-item
      :title="title"
      :to="route"
    />
    <the-o-b-w-scores-list-group />
    <the-s-r-w-scores-list-group />
    <the-b-a-w-scores-list-group />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheBAWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheBAWScoresListGroup.vue";
import TheOBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheOBWScoresListGroup.vue";
import TheSRWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheSRWScoresListGroup.vue";
import { ROUTE_STIMMABGABEVERMERKE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { isBWB } = storeToRefs(useUserStore());

const title = computed(() =>
  isBWB.value ? "Wahlscheine" : "Stimmabgabevermerke"
);

const route = computed(() =>
  isBWB.value ? "Wahlscheine" : ROUTE_STIMMABGABEVERMERKE
);
</script>
