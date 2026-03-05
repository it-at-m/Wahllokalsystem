<template>
  <div class="d-flex flex-row ga-1">
    <v-chip v-if="isVotesVisible">{{ userVotes }}</v-chip>
    <v-chip
      v-if="isInvalidVotesVisible"
      color="error"
      >{{ invalidVotes }}</v-chip
    >
    <v-chip
      v-if="isValidVotesVisible"
      color="success"
      >{{ validVotes }}</v-chip
    >
    <v-chip
      v-if="isWahlvorschlageVotesVisible"
      color="info"
      >{{ wahlvorschlagVotes }}</v-chip
    >
  </div>
</template>

<script setup lang="ts">
import type { StimmzettelKandidat } from "@/types/experimental/StimmzettelKandidat.ts";
import type { PropType } from "vue";

import { computed } from "vue";

const props = defineProps({
  kandidat: {
    type: Object as PropType<StimmzettelKandidat>,
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
  showVotes: {
    type: Boolean,
    default: false,
    required: false,
  },
  showValidVotes: {
    type: Boolean,
    default: true,
    required: false,
  },
  showWahlvorschlagVotes: {
    type: Boolean,
    default: true,
    required: false,
  },
});

const userVotes = computed(() => props.kandidat.votesByVoter);
const wahlvorschlagVotes = computed(() => props.kandidat.votesByWahlvorschlag);

const invalidVotes = computed(() => {
  if (props.kandidat.isDiscarded) {
    return userVotes.value;
  } else {
    return userVotes.value > props.maxValidVotes
      ? userVotes.value - props.maxValidVotes
      : 0;
  }
});
const validVotes = computed(() => {
  if (props.kandidat.isDiscarded) {
    return 0;
  } else {
    return userVotes.value > props.maxValidVotes
      ? props.maxValidVotes
      : userVotes.value;
  }
});

const isInvalidVotesVisible = computed(
  () => props.showInvalidVotes && invalidVotes.value > 0
);
const isValidVotesVisible = computed(
  () => props.showValidVotes && validVotes.value > 0
);
const isVotesVisible = computed(() => props.showVotes && userVotes.value > 0);
const isWahlvorschlageVotesVisible = computed(
  () => props.showWahlvorschlagVotes && wahlvorschlagVotes.value > 0
);
</script>

<style scoped></style>
