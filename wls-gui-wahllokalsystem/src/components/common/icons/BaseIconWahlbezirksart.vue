<template>
  <v-icon
    :icon="wahlartIcon"
    size="large"
  ></v-icon>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { VIcon } from "vuetify/components";

import { useUserStore } from "@/stores/userStore.ts";

const userStore = useUserStore();

const iconMap = {
  UWB: "$wahlbezirksartUWB",
  BWB: "$wahlbezirksartBWB",
};

const wahlartIcon = computed(() => {
  const wahlbezirkArt = userStore.getUser?.wahlbezirksArt ?? "BWB";
  return iconMap[wahlbezirkArt];
});
</script>
