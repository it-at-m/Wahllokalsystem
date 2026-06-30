<template>
  <v-card>
    <v-card-title>Wahlvorschläge</v-card-title>
    <v-card-text>
      <div
        v-if="selectedWahlvorschlaege.length > 0"
        v-for="wahlvorschlag in selectedWahlvorschlaege"
        :key="wahlvorschlag.identifikator"
      >
        {{ wahlvorschlag.ordnungszahl }} - {{ wahlvorschlag.kurzname }}
      </div>
      <div v-else>Keine Listenkreuze gesetzt</div>
      <div v-if="showDebugSelectedWahlvorschlaege">
        Required votes left to fulfil selected wahlvorschlaege:
        {{ requiredVotesLeftToFulfilListenkreuze }}
      </div>
    </v-card-text>

    <v-card-title>Kandidaten</v-card-title>
    <v-card-text>
      <div>Total Kandidaten Votes: {{ totalKandidatenScores }}</div>
      <div>
        Total Invalid Kandidaten Votes: {{ totalInvalidKandidatenScores }}
      </div>
      <div>Total Valid Kandidaten Votes: {{ totalValidKandidatenScores }}</div>
      <div
        v-if="showAllKandidaten"
        v-for="kandidat in stimmzettelKandidaten"
        :key="kandidat.identifikator"
      >
        {{ kandidat.identifikator }}: {{ kandidat.votesByVoter }}
      </div>
      <div>Total Discarded Kandidaten: {{ discardedKandidaten.length }}</div>
      <div
        v-for="kandidat in discardedKandidaten"
        :key="kandidat.identifikator"
      >
        {{ kandidat.identifikator }}
      </div>
    </v-card-text>

    <v-card-title
      >Stimmzettel ist gültig: {{ isStimmzettelValid }}</v-card-title
    >
    <v-card-text>
      <div v-if="!isAtLeastOneScoreGiven">Es wurden keine Stimmen vergeben</div>
      <div v-if="!isMaxVotesFulfilled">
        Es wurden mehr Stimmen vergeben als erlaubt
      </div>
      <div v-if="!isAtLeastOneValidScoreGiven">
        Es gibt nicht eine gültige Stimme
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const wahlId = "wahlId";
const wahlbezirkId = "wahlbezirkId";

const { showAllKandidaten, showDebugSelectedWahlvorschlaege } = defineProps({
  showAllKandidaten: {
    type: Boolean,
    required: false,
    default: false,
  },
  showDebugSelectedWahlvorschlaege: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const {
  isStimmzettelValid,
  isAtLeastOneScoreGiven,
  isAtLeastOneValidScoreGiven,
  isMaxVotesFulfilled,
  stimmzettelKandidaten,
  stimmzettelWahlvorschlaege,
  totalKandidatenScores,
  totalValidKandidatenScores,
  totalInvalidKandidatenScores,
  requiredVotesLeftToFulfilListenkreuze,
} = getStimmzettelManger({
  wahlId,
  wahlbezirkId,
});

const discardedKandidaten = computed(() =>
  stimmzettelKandidaten.value.filter((kandidat) => kandidat.isDiscarded)
);

const selectedWahlvorschlaege = computed(() =>
  stimmzettelWahlvorschlaege.value.filter(
    (wahlvorschlag) => wahlvorschlag.isSelected
  )
);
</script>
