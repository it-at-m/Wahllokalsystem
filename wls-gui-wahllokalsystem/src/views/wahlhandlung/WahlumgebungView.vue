<template>
  <div>
    <the-wahlumgebung-uwb-card v-if="isUWB" />
    <the-wahlumgebung-bwb-card v-if="isBWB" />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheWahlumgebungBwbCard from "@/components/wahlhandlung/TheWahlumgebungBwbCard.vue";
import TheWahlumgebungUwbCard from "@/components/wahlhandlung/TheWahlumgebungUwbCard.vue";
import { useUserStore } from "@/stores/userStore.ts";

const { isUWB, isBWB } = storeToRefs(useUserStore());
</script>
