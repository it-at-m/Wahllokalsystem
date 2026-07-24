<template>
  <v-card>
    <v-card-title>Zusammenfassung</v-card-title>
    <v-card-text class="pb-0">
      <h3>Listenstimmen</h3>
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
      <!--      <div class="text-warning">
        <v-icon
          icon="$alert"
          color="warning"
          size="x-small"
        />
        Reststimmen können nicht eindeutig zugewiesen werden.
      </div>-->
      <v-divider class="my-2" />

      <h3 class="mb-2">Einzelstimmen</h3>
      <v-row align="center">
        <v-col
          class="py-1"
          cols="10"
        >
          <div>Stimmen gesamt:</div>
        </v-col>
        <v-col
          class="py-1"
          cols="2"
        >
          <div class="text-right">{{ totalKandidatenScores }}</div>
        </v-col>
      </v-row>
      <v-row align="center">
        <v-col
          class="py-1"
          cols="10"
        >
          <div>ungültige Stimmen:</div>
        </v-col>
        <v-col class="py-1">
          <div class="text-right">{{ totalInvalidKandidatenScores }}</div>
        </v-col>
      </v-row>
      <v-row align="center">
        <v-col
          class="py-1"
          cols="10"
        >
          <div>direkt vergebene Stimmen:</div>
        </v-col>
        <v-col class="py-1">
          <div class="text-right">{{ totalValidKandidatenScores }}</div>
        </v-col>
      </v-row>
      <v-row align="center">
        <v-col
          class="py-1"
          cols="10"
        >
          <div>Reststimmen:</div>
        </v-col>
        <v-col class="py-1">
          <div class="text-right">{{ totalValidKandidatenScores }}</div>
        </v-col>
      </v-row>
      <v-row align="center">
        <v-col
          class="py-1"
          cols="10"
        >
          <div>Streichungen:</div>
        </v-col>
        <v-col class="py-1">
          <div class="text-right">{{ discardedKandidaten.length }}</div>
        </v-col>
      </v-row>
      <div
        v-for="kandidat in stimmzettelKandidaten"
        v-if="showAllKandidaten"
        :key="kandidat.identifikator"
      >
        {{ kandidat.identifikator }}: {{ kandidat.votesByVoter }}
      </div>
      <div
        v-for="kandidat in discardedKandidaten"
        v-if="showDebugDiscardedKandidaten"
        :key="kandidat.identifikator"
      >
        {{ kandidat.identifikator }}
      </div>
      <v-divider class="my-2" />

      <!-- Beschluss -->
      <!--      <h3>
        <v-icon
          icon="$beschluss"
          color="info"
        />
        Stimmzettel ist für Beschluss vorgemerkt
      </h3>-->
      <!-- Gültigkeit -->
      <h3>
        <v-icon
          :icon="
            isStimmzettelValid ? '$stimmzettelValid' : '$stimmzettelInvalid'
          "
          :color="isStimmzettelValid ? 'success' : 'error'"
        />
        Stimmzettel ist {{ isStimmzettelValid ? "gültig" : "ungültig" }}
      </h3>
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
