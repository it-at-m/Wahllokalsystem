<template>
  <div>
    <the-wahlumgebung-uwb-card v-if="isUWB" />
    <the-wahlumgebung-bwb-card v-if="isBWB" />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheWahlumgebungBwbCard from "@/components/wahlvorbereitung/TheWahlumgebungBwbCard.vue";
import TheWahlumgebungUwbCard from "@/components/wahlvorbereitung/TheWahlumgebungUwbCard.vue";
import { useUserStore } from "@/stores/userStore.ts";

const { isUWB, isBWB } = storeToRefs(useUserStore());
</script>
