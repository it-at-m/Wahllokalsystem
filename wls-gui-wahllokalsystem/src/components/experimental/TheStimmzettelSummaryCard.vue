<template>
  <v-card>
    <v-card-title>Zusammenfassung</v-card-title>
    <v-card-text>
      <h3>Wahlvorschläge</h3>
      <div
        v-for="wahlvorschlag in selectedWahlvorschlaege"
        v-if="selectedWahlvorschlaege.length > 0"
        :key="wahlvorschlag.identifikator"
      >
        {{ wahlvorschlag.ordnungszahl }} - {{ wahlvorschlag.kurzname }}
      </div>
      <div v-else>Keine Listenkreuze gesetzt</div>
      <div v-if="showDebugSelectedWahlvorschlaege">
        Required votes left to fulfil selected wahlvorschlaege:
        {{ requiredVotesLeftToFulfilListenkreuze }}
      </div>
      <div class="text-warning">
        Reststimmen können nicht eindeutig zugewiesen werden.
      </div>
      <v-divider class="my-2" />

      <h3>Kandidaten</h3>
      <div>Stimmen gesamt: {{ totalKandidatenScores }}</div>
      <div>ungültige Stimmen: {{ totalInvalidKandidatenScores }}</div>
      <div>gültige Stimmen: {{ totalValidKandidatenScores }}</div>
      <div
        v-for="kandidat in stimmzettelKandidaten"
        v-if="showAllKandidaten"
        :key="kandidat.identifikator"
      >
        {{ kandidat.identifikator }}: {{ kandidat.votesByVoter }}
      </div>
      <div>gestrichene: {{ discardedKandidaten.length }}</div>
      <div
        v-for="kandidat in discardedKandidaten"
        v-if="showDebugDiscardedKandidaten"
        :key="kandidat.identifikator"
      >
        {{ kandidat.identifikator }}
      </div>
      <v-divider class="my-2" />

      <h3>Gültigkeit: {{ isStimmzettelValid }}</h3>

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

const {
  showAllKandidaten,
  showDebugSelectedWahlvorschlaege,
  showDebugDiscardedKandidaten,
} = defineProps({
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
  showDebugDiscardedKandidaten: {
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
