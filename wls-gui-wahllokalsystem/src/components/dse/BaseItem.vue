<template>
  <div class="d-flex align-center gap-2 candidate-row">
    <div class="slotStyle">
      <slot />
    </div>

    <div class="flex-grow-1 mr-4">
      <div>
        {{ ordnungszahl }}
      </div>
      <div
        :style="{
          textDecoration: isGestrichen ? 'line-through' : 'none',
          maxWidth: '150px',
        }"
        :class="nameStyleClasses"
        class="text-truncate"
      >
        {{ name }}
      </div>
    </div>
    <div class="d-flex flex-column">
      <div class="d-flex">
        <base-chip-anzahl-stimmen
          :stimmen="ungueltigeStimmen ?? 0"
          color="error"
          :hidden="!ungueltigeStimmen"
          class="mr-1"
        />
        <base-chip-anzahl-stimmen
          :stimmen="einzelstimmen ?? 0"
          color="success"
          :hidden="!einzelstimmen"
        />
        <base-chip-anzahl-stimmen
          :stimmen="reststimmen ?? 0"
          color="info"
          :visible="!!reststimmen"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import BaseChipAnzahlStimmen from "@/components/dse/BaseChipAnzahlStimmen.vue";

const props = defineProps<{
  ordnungszahl: number;
  name: string;
  isGestrichen: boolean;
  ungueltigeStimmen: number;
  einzelstimmen: number;
  reststimmen: number;
  nameStyleClasses?: string;
}>();
</script>

<style scoped>
.slotStyle {
  width: 55px;
}
</style>
