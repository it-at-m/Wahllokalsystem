<template>
  <v-row>
    <v-col
      ><base-kandidat-score-operator
        @add="onAddScore"
        @subtract="onSubtractScore"
      />
    </v-col>
    <v-col>
      {{ kandidatenNummer }}
      {{ kandidat.name }}
    </v-col>
    <v-col>
      <base-kandidate-votes :ergebnis="ergebnisModel" />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseKandidateVotes from "@/components/experimental/BaseKandidateVotes.vue";
import BaseKandidatScoreOperator from "@/components/experimental/BaseKandidatScoreOperator.vue";

const ergebnisModel = defineModel("modelValue", {
  type: Object as PropType<Ergebnis>,
  required: true,
});

const props = defineProps({
  listennummer: {
    type: Number,
    required: true,
  },
  kandidat: {
    type: Object as PropType<Kandidat>,
    required: true,
  },
});

const kandidatenNummer = computed(
  () => props.listennummer * 100 + (props.kandidat?.listenposition ?? 0)
);

function onAddScore(count: number) {
  ergebnisModel.value.ergebnis = (ergebnisModel.value.ergebnis ?? 0) + count;
}

function onSubtractScore(count: number) {
  ergebnisModel.value.ergebnis = (ergebnisModel.value.ergebnis ?? 0) - count;
}
</script>
