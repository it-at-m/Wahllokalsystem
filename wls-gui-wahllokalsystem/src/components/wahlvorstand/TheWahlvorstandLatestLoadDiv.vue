<template>
  <div>
    <v-icon icon="$updateTime" />Letzte Aktualisierungszeit:
    {{ time(wahlvorstandStore.lastLoading) }}
  </div>
</template>

<script setup lang="ts">
import { VIcon } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const { time } = useDateTimeFormatter();
const wahlvorstandStore = useWahlvorstandStore();
</script>
