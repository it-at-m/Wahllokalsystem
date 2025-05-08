<template>
    <div class="time">
         {{ currentTime.toLocaleTimeString() }}
    </div>
</template>
<script setup lang="ts">
import { useCurrentTime } from "@/composables/useCurrentTime";

const { currentTime } = useCurrentTime();
</script>

<style>
.time {
  min-width: 100px;
  font-size: 24px;
  font-family: 'Rationale', monospace;
  font-style: italic;
  letter-spacing: 0.05em;
}
</style>
