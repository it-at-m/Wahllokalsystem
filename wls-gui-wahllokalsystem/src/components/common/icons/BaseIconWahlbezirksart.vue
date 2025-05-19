<template>
  <v-icon
    :icon="wahlartIcon"
    size="large"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";
import { VIcon } from "vuetify/components";

import { useUserStore } from "@/stores/userStore.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

const iconMap = {
  UWB: "$wahlbezirksartUWB",
  BWB: "$wahlbezirksartBWB",
};

const wahlartIcon = computed(() => {
  return iconMap[currentUserWahlbezirksArt.value];
});
</script>
