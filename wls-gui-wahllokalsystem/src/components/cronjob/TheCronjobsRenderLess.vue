<script setup lang="ts">
import { onMounted, onUnmounted } from "vue";

import { useBroadcastStore } from "@/stores/broadcastStore.ts";

const { loadLatestMessage } = useBroadcastStore();

const time5MinutesInMilliseconds = 60_000 * 5;

const broadcastMessagePollingInterval = time5MinutesInMilliseconds;
let broadcastMessageActiveInterval: number | null = null;

onMounted(() => {
  startBroadcastMessageInterval();
});

onUnmounted(() => {
  stopBroadcastMessageInterval();
});

function startBroadcastMessageInterval() {
  broadcastMessageActiveInterval = window.setInterval(
    () => loadLatestMessage(),
    broadcastMessagePollingInterval
  );
  loadLatestMessage();
}

function stopBroadcastMessageInterval() {
  if (broadcastMessageActiveInterval !== null) {
    clearInterval(broadcastMessageActiveInterval);
    broadcastMessageActiveInterval = null;
  }
}
</script>
