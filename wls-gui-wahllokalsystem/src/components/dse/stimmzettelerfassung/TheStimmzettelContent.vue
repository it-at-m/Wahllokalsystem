<template>
  <v-sheet class="mx-auto">
    <v-slide-group
      show-arrows
      center-active
      :model-value="activeWahlvorschlagId"
    >
      <v-slide-group-item
        v-for="wv in wahlvorschlaege"
        :key="wv.wahlvorschlagID"
        :value="wv.wahlvorschlagID"
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
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

import BaseWahlvorschlagCard from "./baseComponents/BaseWahlvorschlagCard.vue";

defineProps<{
  activeWahlvorschlagId: string | null;
  activeKandidatId?: string | null;
  wahlvorschlaege: Wahlvorschlag[];
}>();
</script>
