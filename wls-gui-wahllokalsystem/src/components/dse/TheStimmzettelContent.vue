<template>
  <v-sheet class="mx-auto">
    <v-slide-group
      show-arrows
      center-active
      :model-value="activeWahlvorschlagId"
    >
      <v-slide-group-item
        v-for="wv in wahlvorschlaegeSortiert"
        :key="wv.identifikator"
        :value="wv.identifikator"
      >
        <base-wahlvorschlag-card
          :wahlvorschlag="wv"
          :active-kandidat-id="activeKandidatId"
        />
      </v-slide-group-item>
    </v-slide-group>
  </v-sheet>
</template>

<script setup lang="ts">
import type { WahlvorschlagAnzeige } from "@/types/dse/WahlvorschlagAnzeige.ts";

import { computed } from "vue";

import BaseWahlvorschlagCard from "./BaseWahlvorschlagCard.vue";

const props = defineProps<{
  activeWahlvorschlagId: string | null;
  activeKandidatId?: string | null;
  wahlvorschlaege: WahlvorschlagAnzeige[];
}>();

const wahlvorschlaegeSortiert = computed(() => {
  return props.wahlvorschlaege
    .slice()
    .sort((a, b) => a.ordnungszahl - b.ordnungszahl);
});
</script>
