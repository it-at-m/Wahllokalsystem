<template>
  <v-slide-group
    show-arrows
    center-active
    :model-value="activeWahlvorschlagId"
    style="flex: 1 1 auto"
  >
    <v-slide-group-item
      v-for="wv in wahlvorschlaege"
      :key="wv.wahlvorschlagID"
      :value="wv.wahlvorschlagID"
    >
      <base-wahlvorschlag-card
        :wahlvorschlag="wv"
        :active-kandidat="activeKandidat"
      />
    </v-slide-group-item>
  </v-slide-group>
</template>

<script setup lang="ts">
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

import BaseWahlvorschlagCard from "./baseComponents/BaseWahlvorschlagCard.vue";

defineProps<{
  activeWahlvorschlagId: string | null;
  activeKandidat: Kandidat | null;
  wahlvorschlaege: Wahlvorschlag[];
}>();
</script>
