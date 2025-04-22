<template>
  <VIcon
    :icon="wahlartIcon"
    size="large"
  ></VIcon>
</template>

<script setup lang="ts">
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
  if (store.getUser !== null) {
    return iconMap[store.getUser.wahlbezirksArt];
  }
  return iconMap[undefined];
});
</script>
