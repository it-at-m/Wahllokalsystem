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
      <div>
        <span v-if="isDiscarded">🚮</span>
        {{ kandidatenNummer }}
      </div>
      <div>
        {{ kandidat.name }}
      </div>
    </v-col>
    <v-col>
      <base-kandidate-votes :kandidat="kandidat" />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { StimmzettelKandidat } from "@/types/experimental/StimmzettelKandidat.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseKandidateVotes from "@/components/experimental/BaseKandidateVotes.vue";
import BaseKandidatScoreOperator from "@/components/experimental/BaseKandidatScoreOperator.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const props = defineProps({
  listennummer: {
    type: Number,
    required: true,
  },
  kandidat: {
    type: Object as PropType<StimmzettelKandidat>,
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
  stimmzettelManager.discardedKandidaten.value.some(
    (kandidat) => kandidat.identifikator === props.kandidat.identifikator
  )
);

function onAddScore(count: number) {
  stimmzettelManager.addKandidatVote(props.kandidat.identifikator, count);
}

function onSubtractScore(count: number) {
  stimmzettelManager.removeKandidatVote(props.kandidat.identifikator, count);
}

function onRevokeDiscard() {
  stimmzettelManager.revokeDiscardedKandidat(props.kandidat.identifikator);
}

function onDiscard() {
  stimmzettelManager.discardKandidat(props.kandidat.identifikator);
}
</script>
