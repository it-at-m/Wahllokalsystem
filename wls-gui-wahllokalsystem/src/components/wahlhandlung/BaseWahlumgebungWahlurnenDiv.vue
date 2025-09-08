<template>
  <div class="d-flex flex-wrap justify-start">
    <div
      v-for="(wahl, index) in wahlVorbereitung.urnenAnzahl"
      :key="index"
    >
      <v-number-input
        v-model="wahl.anzahl"
        class="mr-4"
        :rules="[required, minNumber(1), maxNumber(99)]"
        :data-test="`textFieldUrnenAnzahl_${index}`"
        :label="`Anzahl der Wahlurnen ${wahlenActions.getWahlNameOrBlankStringById(wahl.wahlID)}`"
        min-width="30rem"
        clearable
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

import { useRules } from "@/composables/common/rules.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { maxNumber, minNumber, required } = useRules();

defineProps<{
  wahlVorbereitung: Wahlvorbereitung;
}>();

const { wahlenActions } = useWahlenStore();
</script>
