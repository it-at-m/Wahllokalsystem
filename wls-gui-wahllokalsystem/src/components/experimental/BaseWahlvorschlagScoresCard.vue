<template>
  <v-card style="min-width: 400px">
    <v-card-title
      >Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}
      <div class="d-flex align-center justify-space-between">
        <v-checkbox
          v-model="wahlvorschlagSelected"
          hide-details
        />
        <div class="d-flex flex-row ga-1">
          <v-chip
            v-if="sumInvalidUserVotesOfWahlvorschlag > 0"
            color="error"
            >{{ sumInvalidUserVotesOfWahlvorschlag }}</v-chip
          >
          <v-chip
            v-if="sumValidUserVotesOfWahlvorschlag > 0"
            color="success"
            >{{ sumValidUserVotesOfWahlvorschlag }}</v-chip
          >
        </div>
      </div>
    </v-card-title>
    <v-card-text>
      <template
        v-for="(kandidat, index) in wahlvorschlag.kandidaten"
        :key="index"
      >
        <base-kandidat-score
          :kandidat="kandidat"
          :listennummer="wahlvorschlag.ordnungszahl"
        />
        <v-divider
          v-if="index < wahlvorschlag.kandidaten.length - 1"
          thickness="2"
          class="my-2"
        />
      </template>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseKandidatScore from "@/components/experimental/BaseKandidatScore.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const props = defineProps({
  wahlvorschlag: {
    type: Object as PropType<StimmzettelWahlvorschlag>,
    required: true,
  },
  totalUserVotes: {
    type: Number,
    required: true,
  },
  maxTotalVotes: {
    type: Number,
    required: true,
  },
  maxValidVotes: {
    type: Number,
    required: false,
    default: 3,
  },
  showInvalidVotes: {
    type: Boolean,
    default: true,
    required: false,
  },
});

const stimmzettelManager = getStimmzettelManger({
  wahlbezirkId: "wahlbezirkId",
  wahlId: "wahlId",
});

const sumValidUserVotesOfWahlvorschlag = computed(() => {
  return props.totalUserVotes > props.maxTotalVotes
    ? 0
    : props.wahlvorschlag.kandidaten
        .filter(
          (kandidat) => kandidat.votesByVoter > 0 && !kandidat.isDiscarded
        )
        .map((kandidat) => Math.min(kandidat.votesByVoter, props.maxValidVotes))
        .reduce((sum, votes) => sum + votes, 0);
});
const sumInvalidUserVotesOfWahlvorschlag = computed(() => {
  if (props.totalUserVotes > props.maxTotalVotes) {
    return props.wahlvorschlag.kandidaten
      .map((kandidat) => kandidat.votesByVoter)
      .reduce((sum, votes) => sum + votes, 0);
  } else {
    return props.wahlvorschlag.kandidaten
      .map((kandidat) => {
        if (kandidat.isDiscarded) {
          return kandidat.votesByVoter;
        } else {
          return kandidat.votesByVoter > props.maxValidVotes
            ? kandidat.votesByVoter - props.maxValidVotes
            : 0;
        }
      })
      .reduce((sum, votes) => sum + votes, 0);
  }
});

const wahlvorschlagSelected = computed({
  set: (value: boolean) => {
    if (value) {
      stimmzettelManager.selectWahlvorschlag(props.wahlvorschlag.identifikator);
    } else {
      stimmzettelManager.deselectWahlvorschlag(
        props.wahlvorschlag.identifikator
      );
    }
  },
  get: () => {
    return stimmzettelManager.selectedWahlvorschlaege.value.some(
      (wahlvorschlag) =>
        wahlvorschlag.identifikator === props.wahlvorschlag.identifikator
    );
  },
});
</script>
