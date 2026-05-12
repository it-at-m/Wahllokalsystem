<template>
  <div>
    <v-tabs-window v-model="subViewBeschlussfassung">
      <v-tabs-window-item value="overview">
        <the-beschlussfassung-overview />
      </v-tabs-window-item>
      <v-tabs-window-item value="erfassung"> Erfassung </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheBeschlussfassungOverview from "@/components/experimental/TheBeschlussfassungOverview.vue";
import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

const { subViewBeschlussfassung } = storeToRefs(useExperimentalFeaturesStore());
</script>
