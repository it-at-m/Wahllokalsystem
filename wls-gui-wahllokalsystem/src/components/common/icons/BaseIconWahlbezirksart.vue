<template>
  <v-icon
    v-if="currentUserWahlbezirksArt"
    :icon="wahlartIcon"
    size="large"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useUserStore } from "@/stores/userStore.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

const iconMap = {
  UWB: "$wahlbezirksartUWB",
  BWB: "$wahlbezirksartBWB",
};

const wahlartIcon = computed(() => {
  return currentUserWahlbezirksArt.value
    ? iconMap[currentUserWahlbezirksArt.value]
    : "";
});
</script>
