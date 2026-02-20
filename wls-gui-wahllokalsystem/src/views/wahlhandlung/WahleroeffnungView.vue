<template>
  <base-wahleroeffnung-card>
    <template #userHint>
      {{ wahleroeffnungsCardTitle }}
    </template>
  </base-wahleroeffnung-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseWahleroeffnungCard from "@/components/wahlhandlung/BaseWahleroeffnungCard.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

const TITLE_UWB =
  "Bitte geben Sie die Uhrzeit ein, zu der mit der Stimmabgabe begonnen wurde.";
const TITLE_BWB =
  "Bitte geben Sie die Uhrzeit ein, zu der der Wahlvorstand zusammengetreten ist.";

const wahleroeffnungsCardTitle = computed(() => {
  switch (currentUserWahlbezirksArt.value) {
    case WahlbezirksArtEnum.UWB:
      return TITLE_UWB;
    case WahlbezirksArtEnum.BWB:
      return TITLE_BWB;
    default:
      return "";
  }
});
</script>
