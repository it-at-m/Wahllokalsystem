<template>
  <div class="d-flex flex-wrap justify-start">
    <div
      v-for="(wahl, index) in wahlVorbereitung.urnenAnzahl"
      :key="index"
    >
      <base-number-input
        v-model="wahl.anzahl"
        class="mr-4"
        :min="1"
        :max="99"
        :rules="[required]"
        :data-test="`textFieldUrnenAnzahl_${index}`"
        :label="`Anzahl der Wahlurnen ${wahlenActions.getWahlNameOrBlankStringById(wahl.wahlID)}`"
        min-width="30rem"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { required } = useRules();

defineProps<{
  wahlVorbereitung: Wahlvorbereitung;
}>();

const { wahlenActions } = useWahlenStore();
</script>
