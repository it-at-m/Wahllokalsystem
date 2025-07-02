import { useBroadcastStore } from "@/stores/broadcastStore.ts";

export function useBroadcastCronjobService() {
  const { loadLatestMessage } = useBroadcastStore();

  const time5MinutesInMilliseconds = 60_000 * 5;

  const broadcastMessagePollingInterval = time5MinutesInMilliseconds;
  let broadcastMessageActiveInterval: number | null = null;

  function startBroadcastMessageInterval() {
    if (broadcastMessageActiveInterval !== null) {
      stopBroadcastMessageInterval();
    }

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

  return {
    startBroadcastMessageInterval,
    stopBroadcastMessageInterval,
  };
}
