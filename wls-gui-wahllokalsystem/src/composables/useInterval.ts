import { onMounted, onUnmounted, ref } from "vue";

import { useSchedulerStore } from "@/stores/schedulerStore.ts";
import { IntervalConfiguration } from "@/types/scheduler/IntervalConfiguration.ts";

export function useInterval(
  title: string,
  callback: () => void,
  delay: number
) {
  const { registerInterval, stopInterval } = useSchedulerStore();
  const intervalId = ref<null | number>(null);

  const start = () => {
    if (intervalId.value === null) {
      intervalId.value = registerInterval(
        new IntervalConfiguration(title, callback, delay)
      );
    }
  };

  const stop = () => {
    if (intervalId.value !== null) {
      stopInterval(intervalId.value);
      intervalId.value = null;
    }
  };

  onMounted(() => {
    start();
  });

  onUnmounted(() => {
    stop();
  });

  return { start, stop };
}
