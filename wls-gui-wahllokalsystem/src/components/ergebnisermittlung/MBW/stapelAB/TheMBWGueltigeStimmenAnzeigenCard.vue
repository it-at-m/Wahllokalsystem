<template>
  <v-card>
    <v-card-title> Gültige Stimmen </v-card-title>
    <v-card-text>
      <the-m-b-w-gueltige-stimmen-anzeigen-table
        v-if="wahlbezirkID && wahlID"
        v-model="modelValue"
      />
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import TheMBWGueltigeStimmenAnzeigenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenTable.vue";

const modelValue = defineModel({
  type: Object as PropType<MbwErgebnisseAndWahlvorschlag[]>,
  required: true,
});

defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
});
</script>
