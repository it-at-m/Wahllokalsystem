<template>
  <base-item
    :is-gestrichen="kandidat.durchgestrichen"
    :name="kandidat.name"
    :ordnungszahl="kandidat.ordnungszahl"
    :einzelstimmen="kandidat.einzelstimmen ?? 0"
    :ungueltige-stimmen="kandidat.ungueltigeStimmen ?? 0"
    :reststimmen="kandidat.reststimmen ?? 0"
  >
    <span
      :class="`border pa-1 px-4 d-inline-flex align-center justify-center rounded me-3 font-weight-medium ${gesamtStimmen === 0 ? 'text-transparent' : ''}`"
    >
      {{ gesamtStimmen }}
    </span>
  </base-item>
</template>
<script setup lang="ts">
import type { Kandidat } from "@/types/dse/Kandidat.ts";

import { computed } from "vue";

import BaseChipAnzahlStimmen from "@/components/dse/BaseChipAnzahlStimmen.vue";
import BaseItem from "@/components/dse/BaseItem.vue";

const props = defineProps<{
  kandidat: Kandidat;
}>();

const gesamtStimmen = computed(
  () =>
    (props.kandidat.reststimmen ?? 0) +
    (props.kandidat.einzelstimmen ?? 0) +
    (props.kandidat.ungueltigeStimmen ?? 0)
);
</script>
