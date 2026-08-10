<template>
  <div
      class="text-subtitle-2 mb-1">
    <v-icon
      icon="$updateTime"
      class="mr-2 "
    />
    Letzte Aktualisierungszeit: {{ toHhMmSs(lastLoadingDate) }}
  </div>
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

defineProps({
  lastLoadingDate: {
    type: [Date, null] as PropType<Date | null>,
    default: null,
  },
});
const { toHhMmSs } = useDateTimeFormatter();
</script>
