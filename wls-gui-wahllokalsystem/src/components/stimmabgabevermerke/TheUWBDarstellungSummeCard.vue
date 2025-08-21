<template>
  <v-card>
    <v-card-title> Anzahl Stimmabgabevermerke zusammen</v-card-title>
    <v-card-text><the-u-w-b-darstellung-summe-table /> </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import TheUWBDarstellungSummeTable from "@/components/stimmabgabevermerke/TheUWBDarstellungSummeTable.vue";
</script>
