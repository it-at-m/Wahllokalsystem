<template>
  <div>
    <v-icon icon="$updateTime" />Letzte Aktualisierungszeit:
    {{ time(wahlvorstandStore.lastLoading) }}
  </div>
</template>

<script setup lang="ts">
import { VIcon } from "vuetify/components";

import useFormatter from "@/composables/common/formatter";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const { time } = useFormatter();
const wahlvorstandStore = useWahlvorstandStore();
</script>

<style scoped></style>
