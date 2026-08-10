<template>
  <base-latest-load-div :last-loading-date="wahlvorstandStore.lastLoading" />
</template>

<script setup lang="ts">
import BaseLatestLoadDiv from "@/components/common/div/BaseLatestLoadDiv.vue";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const wahlvorstandStore = useWahlvorstandStore();
</script>
