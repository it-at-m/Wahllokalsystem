<template>
  <v-card>
    <v-card-title>
      Anzahl Stimmabgabevermerke bei den eingenommenen
      Wahlscheinen</v-card-title
    >
    <v-card-text
      ><the-u-w-b-stimmabgabevermerke-eingenommene-wahlscheine-table />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable.vue";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";

const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
</script>

<style scoped></style>
