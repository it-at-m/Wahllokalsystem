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
