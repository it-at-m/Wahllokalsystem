<template>
    <div class="time">
         {{ currentTime.toLocaleTimeString() }}
    </div>
</template>
<script type="module">
import { useCurrentTime } from "@/composables/useCurrentTime";
export default {
  setup() {
    const { currentTime } = useCurrentTime();

    return { currentTime };
  },
};
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
