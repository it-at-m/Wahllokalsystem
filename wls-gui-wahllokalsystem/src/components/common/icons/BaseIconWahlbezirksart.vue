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
  [undefined]: "$wahlbezirksartBWB",
};

const wahlartIcon = computed(() => {
  console.log("Trigger fuer Change aufgerufen:", store.getWahlbezirksArt);
  return iconMap[store.getWahlbezirksArt];
});
</script>
