<template>
  <div>
    <div>
      <v-row
        class="align-center text-center"
        size="12"
      >
        <v-col cols="2">
          <base-button-kandidat-discard
            v-model="isDiscarded"
            :use-switch-component="true"
          />
        </v-col>
        <v-col
          class="text-end"
          cols="2"
          >{{ kandidatenNummer }}</v-col
        >
        <v-col cols="5">
          <base-kandidat-score-operator
            v-model="userVotes"
            v-model:is-discarded="isDiscarded"
            @add="onAddScore"
            @subtract="onSubtractScore"
            @discard="onDiscard"
            @revoke-discard="onRevokeDiscard"
          />
        </v-col>
        <v-col cols="3">
          <base-kandidate-votes
            class="justify-end"
            :kandidat="kandidat"
          />
        </v-col>
      </v-row>
      <div v-if="showKandidatName">
        {{ kandidat.name }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { StimmzettelKandidat } from "@/types/experimental/StimmzettelKandidat.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseButtonKandidatDiscard from "@/components/experimental/BaseButtonKandidatDiscard.vue";
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
  showKandidatName: {
    type: Boolean,
    default: true,
    required: false,
  },
});

const userVotes = computed({
  get: () => props.kandidat.votesByVoter,
  set: (newValue: number) =>
    stimmzettelManager.setKandidatVote(props.kandidat.identifikator, newValue),
});

const kandidatenNummer = computed(
  () => props.listennummer * 100 + (props.kandidat?.listenposition ?? 0)
);

const stimmzettelManager = getStimmzettelManger({
  wahlbezirkId: "wahlbezirkId",
  wahlId: "wahlId",
});
const isDiscarded = computed({
  get: () => props.kandidat.isDiscarded,
  set: (newValue: boolean) =>
    stimmzettelManager.discardKandidat(props.kandidat.identifikator, newValue),
});

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
