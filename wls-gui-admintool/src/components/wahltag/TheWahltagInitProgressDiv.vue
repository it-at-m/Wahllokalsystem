<template>
  <v-card variant="flat">
    <v-card-title v-if="!loadingIsFinished"
      >Wahldaten für Wahltag {{ progressVorschlaege?.forWahltag }} mit
      Wahlnummer {{ progressVorschlaege?.wahlNummer }} werden im Hintergrund
      geladen...</v-card-title
    >
    <v-card-title v-else>
      Wahldaten für Wahltag {{ progressVorschlaege?.forWahltag }} mit Wahlnummer
      {{ progressVorschlaege?.wahlNummer }} wurden am
      {{ progressVorschlaege?.lastFinishTime }}
      erfolgreich geladen.
    </v-card-title>
    <v-card-text v-if="!loadingIsFinished">
      <base-init-progress
        :awerte="progressAWerte"
        :referendumvorschlaege="progressVorschlaege?.referendumvorlagen"
        :wahlvorschlaege="progressVorschlaege?.wahlvorschlaege"
        class="mt-3"
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { AWerteInitProgress } from "@/types/aWerte/AWerteInitProgress.ts";
import type { BasisdatenInitProgress } from "@/types/basisdaten/BasisdatenInitProgress.ts";
import type { Ref } from "vue";

import { computed, onMounted, onUnmounted, ref } from "vue";
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import BaseInitProgress from "@/components/wahltag/BaseInitProgress.vue";
import { useAWerteService } from "@/composables/aWerte/aWerteService.ts";
import { useBasisdatenService } from "@/composables/basisdaten/basisdatenService.ts";

const { getAsyncProgress } = useBasisdatenService();
const { getAWerteProgress } = useAWerteService();

const pollIntervalInMilliseconds = 500;
let activePollInterval: number | null = null;

const progressVorschlaege: Ref<BasisdatenInitProgress | undefined> =
  ref(undefined);
const progressAWerte: Ref<AWerteInitProgress | undefined> = ref(undefined);
const loadingIsFinished = computed(
  () =>
    progressVorschlaege.value?.lastFinishTime &&
    progressAWerte.value?.lastFinishTime
);

onMounted(() => {
  loadInitProgress();
  startProgressPollInterval();
});

onUnmounted(() => {
  stopProgressPollInterval();
});

function loadInitProgress() {
  getAWerteProgress().then((progress) => {
    progressAWerte.value = progress;
  });
  getAsyncProgress().then((progress) => {
    progressVorschlaege.value = progress;
  });
}

function startProgressPollInterval() {
  activePollInterval = window.setInterval(
    () => loadInitProgress(),
    pollIntervalInMilliseconds
  );
}

function stopProgressPollInterval() {
  if (activePollInterval !== null) {
    clearInterval(activePollInterval);
    activePollInterval = null;
  }
}
</script>
