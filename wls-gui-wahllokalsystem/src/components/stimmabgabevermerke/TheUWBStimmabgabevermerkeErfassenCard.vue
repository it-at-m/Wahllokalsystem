<script setup lang="ts">
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import TheUWBStimmabgabevermerkeErfassenTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenTable.vue";
import TheUWBStimmabgabevermerkeErfassenTableTest from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenTableTest.vue";
</script>

<template>
  <v-card>
    <v-card-title> Anzahl der Blätter im Wählerverzeichnis</v-card-title>
    <v-card-text><the-u-w-b-stimmabgabevermerke-erfassen-table /> </v-card-text>
    <the-u-w-b-stimmabgabevermerke-erfassen-table-test />
  </v-card>
</template>

<style scoped></style>
