<template>
  <div class="d-flex align-center gap-2 candidate-row">
    <span
      :class="`border pa-1 px-4 d-inline-flex align-center justify-center rounded me-3 font-weight-medium ${Number(kandidat.gesamtStimmen) === 0 ? 'text-transparent' : ''}`"
    >
      {{ kandidat.gesamtStimmen }}
    </span>

    <div class="flex-grow-1 mr-4">
      <div>
        {{ kandidat.identifikator }}
      </div>
      <div
        :style="{
          textDecoration: kandidat.durchgestrichen ? 'line-through' : 'none',
          maxWidth: '350px'
        }"
        class="text-truncate"
      >
        {{ kandidat.name }}
      </div>
    </div>
    <div class="d-flex flex-column">
      <div class="d-flex">
        <base-chip-anzahl-stimmen
          :stimmen="kandidat.ungueltigeStimmen"
          color="error"
          :hidden="!kandidat.ungueltigeStimmen"
          class="mr-1"
        />
        <base-chip-anzahl-stimmen
          :stimmen="kandidat.gueltigeStimmen"
          color="success"
          :hidden="!kandidat.gueltigeStimmen"
        />
        <base-chip-anzahl-stimmen
          :stimmen="kandidat.restStimmen"
          color="info"
          :visible="!!kandidat.restStimmen"
        />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { KandidatAnzeige } from "@/types/dse/KandidatAnzeige.ts";

import BaseChipAnzahlStimmen from "@/components/dse/BaseChipAnzahlStimmen.vue";

defineProps<{
  kandidat: KandidatAnzeige;
}>();
</script>
