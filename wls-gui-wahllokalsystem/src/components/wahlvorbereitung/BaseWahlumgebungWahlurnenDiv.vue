<template>
  <div class="d-flex flex-wrap justify-start">
    <div
      v-for="(wahl, index) in wahlVorbereitung.urnenAnzahl"
      :key="index"
    >
      <v-number-input
        v-model="wahl.anzahl"
        class="mr-4"
        :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(99)]"
        :data-test="`textFieldUrnenAnzahl_${index}`"
        :label="`Anzahl der Wahlurnen ${getWahlNameOrBlankStringById(wahl.wahlID)}`"
        min-width="30rem"
        clearable
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Wahlvorbereitung } from "@/types/wahlvorbereitung/Wahlvorbereitung.ts";

import { VNumberInput } from "vuetify/components";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

defineProps<{
  wahlVorbereitung: Wahlvorbereitung;
}>();

const { getWahlNameOrBlankStringById } = useWahlenStore();
</script>
