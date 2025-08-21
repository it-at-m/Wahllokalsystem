<template>
  <v-card>
    <v-card-title> Anzahl der Blätter im Wählerverzeichnis</v-card-title>
    <the-u-w-b-stimmabgabevermerke-erfassen-table />
  </v-card>
</template>

<script setup lang="ts">
import { VCard, VCardTitle } from "vuetify/components";

import TheUWBStimmabgabevermerkeErfassenTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenTable.vue";
</script>
