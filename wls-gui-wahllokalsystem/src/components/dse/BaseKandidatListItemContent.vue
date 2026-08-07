<template>
  <div class="d-flex align-center gap-2 candidate-row">
    <span
      class="border pa-1 pr-4 pl-4 d-inline-flex align-center justify-center rounded me-3 font-weight-medium"
      :title="String(anzahlStimmen)"
    >
      {{ anzahlStimmen }}
    </span>

    <div class="flex-grow-1 mr-4">
      <div class="">
        {{ id }}
      </div>
      <div
        :style="{ textDecoration: durchgestrichen ? 'line-through' : 'none' }"
      >
        {{ name }}
      </div>
    </div>
    <div class="d-flex flex-column">
      <div class="d-flex">
        <v-chip
          size="small"
          color="error"
          variant="tonal"
          class="mr-1"
          :style="!ungueltigeStimmen ? 'visibility: hidden' : ''"
          :aria-hidden="!ungueltigeStimmen"
        >
          {{ ungueltigeStimmen }}
        </v-chip>
        <v-chip
          size="small"
          color="success"
          variant="tonal"
          :style="!gueltigeStimmen ? 'visibility: hidden' : ''"
          :aria-hidden="!gueltigeStimmen"
          class="mr-1"
        >
          {{ gueltigeStimmen }}
        </v-chip>
        <v-chip
          v-if="restStimmenWahlvorschlag"
          size="small"
          color="info"
          variant="tonal"
        >
          {{ restStimmenWahlvorschlag }}
        </v-chip>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
defineProps<{
  id: string;
  name: string;
  anzahlStimmen: number;
  ungueltigeStimmen: number;
  gueltigeStimmen: number;
  restStimmenWahlvorschlag: number;
  durchgestrichen: boolean;
}>();
</script>
