<template>
  <v-card>
    <v-card-title>Wahlvorschläge</v-card-title>
    <v-card-text>
      <div
        v-for="wahlvorschlagId in selectedWahlvorschlaege"
        :key="wahlvorschlagId"
      >
        {{ wahlvorschlagId }}
      </div>
      <div>
        Required votes left to fulfil selected wahlvorschlaege:
        {{ requiredVotesLeftToFulfilListenkreuze }}
      </div>
    </v-card-text>

    <v-card-title>Kandidaten</v-card-title>
    <v-card-text>
      <div>Total Kandidaten Votes: {{ totalKandidatenScores }}</div>
      <div>Total Valid Kandidaten Votes: {{ totalValidKandidatenScores }}</div>
      <div
        v-for="kandidat in kandidatenScores"
        :key="kandidat.kandidatId"
      >
        {{ kandidat.kandidatId }}: {{ kandidat.votes }}
      </div>
      <div>Total Discarded Kandidaten: {{ discardedKandidatenIds.length }}</div>
      <div
        v-for="kandidatId in discardedKandidatenIds"
        :key="kandidatId"
      >
        {{ kandidatId }}
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const wahlId = "wahlId";
const wahlbezirkId = "wahlbezirkId";

const {
  selectedWahlvorschlaege,
  kandidatenScores,
  totalKandidatenScores,
  totalValidKandidatenScores,
  discardedKandidatenIds,
  requiredVotesLeftToFulfilListenkreuze,
} = getStimmzettelManger({
  wahlId,
  wahlbezirkId,
});
</script>
