<template>
  <div>
    <v-icon
      icon="$updateTime"
      class="mr-2"
    />
    Letzte Aktualisierungszeit: {{ toHhMmSs(wahlvorstandStore.lastLoading) }}
  </div>
</template>

<script setup lang="ts">
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const { toHhMmSs } = useDateTimeFormatter();
const wahlvorstandStore = useWahlvorstandStore();
</script>
