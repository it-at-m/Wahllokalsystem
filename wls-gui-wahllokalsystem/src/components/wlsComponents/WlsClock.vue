<template>
  <div class="time">
    {{
      currentTime.toLocaleTimeString(undefined, {
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false,
      })
    }}
  </div>
</template>
<script setup lang="ts">
import { useCurrentTime } from "@/composables/useCurrentTime";

const { currentTime } = useCurrentTime();
</script>
