<template>
  <v-card>
    <the-unguetilge-wahlscheine-verify-card />
    <base-wahlschliessung-card />
  </v-card>
</template>

<script setup lang="ts">
import { VCard } from "vuetify/components";

import BaseWahlschliessungCard from "@/components/wahlvorbereitung/BaseWahlschliessungCard.vue";
import TheUnguetilgeWahlscheineVerifyCard from "@/components/wahlvorbereitung/TheUnguetilgeWahlscheineVerifyCard.vue";
</script>
