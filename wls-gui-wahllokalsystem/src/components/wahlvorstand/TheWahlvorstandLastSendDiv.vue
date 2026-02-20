<template>
  <div>
    <v-icon
      icon="$send"
      class="mr-2"
    />
    Letzte Absendezeit: {{ toHhMmSs(wahlvorstandStore.lastSending) }}
  </div>
</template>

<script setup lang="ts">
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const { toHhMmSs } = useDateTimeFormatter();
const wahlvorstandStore = useWahlvorstandStore();
</script>
