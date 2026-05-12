<template>
  <v-tabs-window
    v-model="subViewBeschlussfassung"
    class="pa-1"
  >
    <v-tabs-window-item value="overview">
      <the-beschlussfassung-overview />
    </v-tabs-window-item>
    <v-tabs-window-item value="erfassung">
      <the-beschlussfassung-edit-card />
    </v-tabs-window-item>
  </v-tabs-window>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheBeschlussfassungEditCard from "@/components/experimental/TheBeschlussfassungEditCard.vue";
import TheBeschlussfassungOverview from "@/components/experimental/TheBeschlussfassungOverview.vue";
import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

const { subViewBeschlussfassung } = storeToRefs(useExperimentalFeaturesStore());
</script>
