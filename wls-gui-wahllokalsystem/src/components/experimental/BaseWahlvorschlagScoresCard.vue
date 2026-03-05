<template>
  <v-card style="min-width: 400px">
    <v-card-title
      >Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}
      <v-checkbox v-model="wahlvorschlagSelected"
    /></v-card-title>
    <v-card-text>
      <base-kandidat-score
        v-for="(kandidat, index) in wahlvorschlag.kandidaten"
        :key="index"
        :kandidat="kandidat"
        :listennummer="wahlvorschlag.ordnungszahl"
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseKandidatScore from "@/components/experimental/BaseKandidatScore.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const props = defineProps({
  wahlvorschlag: {
    type: Object as PropType<StimmzettelWahlvorschlag>,
    required: true,
  },
});

const stimmzettelManager = getStimmzettelManger({
  wahlbezirkId: "wahlbezirkId",
  wahlId: "wahlId",
});

const wahlvorschlagSelected = computed({
  set: (value: boolean) => {
    if (value) {
      stimmzettelManager.selectWahlvorschlag(props.wahlvorschlag.identifikator);
    } else {
      stimmzettelManager.deselectWahlvorschlag(
        props.wahlvorschlag.identifikator
      );
    }
  },
  get: () => {
    return stimmzettelManager.selectedWahlvorschlaege.value.some(
      (wahlvorschlag) =>
        wahlvorschlag.identifikator === props.wahlvorschlag.identifikator
    );
  },
});
</script>
