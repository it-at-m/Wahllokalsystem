<template>
  <div class="time">
    {{
      currentTime.toLocaleTimeString(undefined, {
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      })
    }}
  </div>
</template>
<script setup lang="ts">
import { useCurrentTime } from "@/composables/useCurrentTime";

const { currentTime } = useCurrentTime();
</script>

<style scoped>
.time {
  font-size: 24px;
  font-family: "Rationale", monospace;
  font-style: italic;
  letter-spacing: 0.08em;
}
</style>
