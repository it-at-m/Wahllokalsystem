<template>
  <div class="d-flex align-center gap-2 candidate-row">
    <span
      :class="`border pa-1 px-4 d-inline-flex align-center justify-center rounded me-3 font-weight-medium ${gesamtStimmen === 0 ? 'text-transparent' : ''}`"
    >
      {{ gesamtStimmen }}
    </span>

    <div class="flex-grow-1 mr-4">
      <div>
        {{ kandidatOrdnungszahl }}
      </div>
      <div
        :style="{
          textDecoration: kandidat.durchgestrichen ? 'line-through' : 'none',
          maxWidth: '150px',
        }"
        class="text-truncate"
      >
        {{ kandidat.name }}
      </div>
    </div>
    <div class="d-flex flex-column">
      <div class="d-flex">
        <base-chip-anzahl-stimmen
          :stimmen="kandidat.ungueltigeStimmen ?? 0"
          color="error"
          :hidden="!kandidat.ungueltigeStimmen"
          class="mr-1"
        />
        <base-chip-anzahl-stimmen
          :stimmen="kandidat.einzelstimmen ?? 0"
          color="success"
          :hidden="!kandidat.einzelstimmen"
        />
        <base-chip-anzahl-stimmen
          :stimmen="kandidat.reststimmen ?? 0"
          color="info"
          :visible="!!kandidat.reststimmen"
        />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { Kandidat } from "@/types/dse/Kandidat.ts";

import { computed } from "vue";

import BaseChipAnzahlStimmen from "@/components/dse/BaseChipAnzahlStimmen.vue";

const props = defineProps<{
  kandidat: Kandidat;
}>();

const gesamtStimmen = computed(
  () =>
    (props.kandidat.reststimmen ?? 0) +
    (props.kandidat.einzelstimmen ?? 0) +
    (props.kandidat.ungueltigeStimmen ?? 0)
);

const kandidatOrdnungszahl = computed(
  () =>
    props.kandidat.owningWahlvorschlag.ordnungszahl * 100 +
    props.kandidat.listenposition //TODO die 100 mit dem Multiplikator zusammenlegen. Teil von AZs von 3141
);
</script>
