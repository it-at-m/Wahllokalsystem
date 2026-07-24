<template>
  <div>
    <div>
      <v-row
        class="align-center text-center"
        size="12"
      >
        <v-col cols="2">
          <div class="userVotesBox">
            {{ userVotes == 0 ? "" : userVotes }}
          </div>
        </v-col>
        <v-col
          cols="8"
          class="text-start"
        >
          {{ kandidatenNummer }}
          <div v-if="showKandidatName && !isDiscarded">
            {{ kandidat.name }}
          </div>
          <div
            v-else
            class="text-decoration-line-through"
          >
            {{ kandidat.name }}
          </div>
        </v-col>

        <v-col cols="2">
          <base-kandidate-votes
            class="justify-end"
            :kandidat="kandidat"
          />
        </v-col>
      </v-row>
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
</script>

<style scoped>
.userVotesBox {
  border: 1px solid #ababab;
  border-radius: 2px;
  width: 30px;
  height: 25px;
}
</style>
