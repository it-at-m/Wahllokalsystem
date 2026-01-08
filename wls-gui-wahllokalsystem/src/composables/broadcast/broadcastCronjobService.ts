import { useBroadcastStore } from "@/stores/broadcastStore.ts";
import { useSchedulerStore } from "@/stores/schedulerStore.ts";
import { IntervalConfiguration } from "@/types/scheduler/IntervalConfiguration.ts";

export function useBroadcastCronjobService() {
  const { loadLatestMessage } = useBroadcastStore();
  const { registerInterval, stopInterval } = useSchedulerStore();

  const time5MinutesInMilliseconds = 60_000 * 5;

  const broadcastMessagePollingInterval = time5MinutesInMilliseconds;
  let broadcastMessageActiveInterval: number | null = null;

  function startBroadcastMessageInterval() {
    if (broadcastMessageActiveInterval !== null) {
      stopBroadcastMessageInterval();
    }

    broadcastMessageActiveInterval = registerInterval(
      new IntervalConfiguration(
        "Broadcast Message Interval",
        loadLatestMessage,
        broadcastMessagePollingInterval,
        true
      )
    );
  }

  function stopBroadcastMessageInterval() {
    if (broadcastMessageActiveInterval !== null) {
      stopInterval(broadcastMessageActiveInterval);
      broadcastMessageActiveInterval = null;
    }
  }

  return {
    startBroadcastMessageInterval,
    stopBroadcastMessageInterval,
  };
}
