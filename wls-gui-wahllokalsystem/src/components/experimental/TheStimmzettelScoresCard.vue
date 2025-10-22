<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-row>
        <v-col
          v-for="(wahlvorschlag, index) in wahlvorschlaege.wahlvorschlaege"
          :key="index"
        >
          <base-wahlvorschlag-scores-card :wahlvorschlag="wahlvorschlag" />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import BaseWahlvorschlagScoresCard from "@/components/experimental/BaseWahlvorschlagScoresCard.vue";

defineProps({
  wahlvorschlaege: {
    type: Object as PropType<Wahlvorschlaege>,
    required: true,
  },
});

const ergebnisse = ref<Ergebnis[]>([]);
</script>

<style scoped></style>
