<template>
  <div>
    <v-card-title>
      {{ title }}
    </v-card-title>
    <v-card-text>
      <the-u-w-b-stimmabgabevermerke-eingenommene-wahlscheine-table />
    </v-card-text>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlenState } = storeToRefs(useWahlenStore());

const title = computed(() => {
  const anzahlWahlen = wahlenState.value?.wahlen?.length;

  if (anzahlWahlen == null) return "";

  return anzahlWahlen > 1
    ? "Anzahl Stimmabgabevermerke bei den eingenommenen Wahlscheinen"
    : anzahlWahlen === 1
      ? "Anzahl der eingenommenen Wahlscheine"
      : "";
});
</script>
