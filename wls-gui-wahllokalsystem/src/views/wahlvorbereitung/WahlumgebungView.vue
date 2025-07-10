<template>
  <wahlumgebung-uwb-card
    v-if="currentUserWahlbezirksArt == WahlbezirksArtEnum.UWB"
  />
  <wahlumgebung-bwb-card
    v-if="currentUserWahlbezirksArt == WahlbezirksArtEnum.BWB"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import WahlumgebungBwbCard from "@/components/wahlvorbereitung/WahlumgebungBwbCard.vue";
import WahlumgebungUwbCard from "@/components/wahlvorbereitung/WahlumgebungUwbCard.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
</script>
