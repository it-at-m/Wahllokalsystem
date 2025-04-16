<template>
  <VIcon
    :icon="wahlartIcon"
    size="large"
  ></VIcon>
</template>

<script setup>
import { computed } from "vue";
import { VIcon } from "vuetify/components";

import { useUserStore } from "@/stores/user";

const store = useUserStore();

const iconMap = {
  UWB: "$wahlbezirksartUWB",
  BWB: "$wahlbezirksartBWB",
};

const wahlartIcon = computed(() => {
  return iconMap[store.getWahlbezirksArt];
});
</script>
