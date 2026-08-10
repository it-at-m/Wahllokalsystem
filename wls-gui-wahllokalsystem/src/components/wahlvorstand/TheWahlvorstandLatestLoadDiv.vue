<template>
  <base-latest-load-div :last-loading-date="wahlvorstandStore.lastLoading" />
</template>

<script setup lang="ts">
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";
import BaseLatestLoadDiv from "@/components/common/div/BaseLatestLoadDiv.vue";

const wahlvorstandStore = useWahlvorstandStore();
</script>
