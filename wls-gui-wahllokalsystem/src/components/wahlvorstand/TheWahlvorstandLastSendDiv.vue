<template>
  <div>
    <v-icon icon="$send" /> Letzte Absendezeit:
    {{ time(wahlvorstandStore.lastSending) }}
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
