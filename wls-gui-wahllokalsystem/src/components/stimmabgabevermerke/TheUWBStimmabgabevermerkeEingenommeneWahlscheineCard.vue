<template>
  <div>
    <v-card-title>
      Anzahl Stimmabgabevermerke bei den eingenommenen Wahlscheinen
    </v-card-title>
    <v-card-text>
      <the-u-w-b-stimmabgabevermerke-eingenommene-wahlscheine-table />
    </v-card-text>
  </div>
</template>

<script setup lang="ts">
import TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable.vue";
</script>
