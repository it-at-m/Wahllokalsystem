<template>
  <v-icon
    :icon="getWahlartIcon()"
    size="large"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { VIcon } from "vuetify/components";

import { useUserStore } from "@/stores/userStore.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

const iconMap = {
  UWB: "$wahlbezirksartUWB",
  BWB: "$wahlbezirksartBWB",
};

function getWahlartIcon(): string {
  return iconMap[currentUserWahlbezirksArt.value];
}
</script>
