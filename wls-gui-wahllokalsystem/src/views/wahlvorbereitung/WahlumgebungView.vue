<template>
  <div>
    <the-wahlumgebung-uwb-card
      v-if="currentUserWahlbezirksArt == WahlbezirksArtEnum.UWB"
    />
    <the-wahlumgebung-bwb-card
      v-if="currentUserWahlbezirksArt == WahlbezirksArtEnum.BWB"
    />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheWahlumgebungBwbCard from "@/components/wahlvorbereitung/TheWahlumgebungBwbCard.vue";
import TheWahlumgebungUwbCard from "@/components/wahlvorbereitung/TheWahlumgebungUwbCard.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
</script>
