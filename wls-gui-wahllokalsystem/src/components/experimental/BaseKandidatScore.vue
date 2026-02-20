<template>
  <v-row>
    <v-col
      ><base-kandidat-score-operator
        @add="onAddScore"
        @subtract="onSubtractScore"
        @discard="onDiscard"
        @revoke-discard="onRevokeDiscard"
      />
    </v-col>
    <v-col>
      <span v-if="isDiscarded">🚮</span>
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
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

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

const stimmzettelManager = getStimmzettelManger({
  wahlbezirkId: "wahlbezirkId",
  wahlId: "wahlId",
});
const isDiscarded = computed(() =>
  stimmzettelManager.discardedKandidatenIds.value.some(
    (id) => id === props.kandidat.identifikator
  )
);

function onAddScore(count: number) {
  ergebnisModel.value.ergebnis = (ergebnisModel.value.ergebnis ?? 0) + count;
  stimmzettelManager.addKandidatVote(props.kandidat.identifikator);
}

function onSubtractScore(count: number) {
  ergebnisModel.value.ergebnis = (ergebnisModel.value.ergebnis ?? 0) - count;
  stimmzettelManager.removeKandidatVote(props.kandidat.identifikator);
}

function onRevokeDiscard() {
  stimmzettelManager.revokeDiscardedKandidat(props.kandidat.identifikator);
}

function onDiscard() {
  stimmzettelManager.discardKandidat(props.kandidat.identifikator);
}
</script>
