import type { IntervalConfiguration } from "@/types/scheduler/IntervalConfiguration.ts";
import type { TimeoutConfiguration } from "@/types/scheduler/TimeoutConfiguration.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";

const storeId = "scheduler";
const { registerStoreHMR } = useHmrUpdate();

export const useSchedulerStore = defineStore(storeId, () => {
  const MIN_DELAY_IN_MILLISECONDS = 0;
  const MAX_DELAY_IN_MILLISECONDS = 0x7fffffff; //https://mrcoles.com/maximum-delay-settimeout/

  const intervals = ref<Map<number, IntervalConfiguration>>(
    new Map<number, IntervalConfiguration>()
  );
  const timeouts = ref<Map<number, TimeoutConfiguration>>(
    new Map<number, TimeoutConfiguration>()
  );

  function registerInterval(interval: IntervalConfiguration): number {
    const id = window.setInterval(() => interval.action(), interval.delay);
    if (interval.runActionAfterRegister) {
      interval.action();
    }
    intervals.value.set(id, interval);

    return id;
  }

  function registerTimeout(timeout: TimeoutConfiguration): number {
    const delayInMilliseconds =
      timeout.dateOfAction.getTime() - new Date().getTime();
    const boundedDelay = Math.max(
      MIN_DELAY_IN_MILLISECONDS,
      Math.min(delayInMilliseconds, MAX_DELAY_IN_MILLISECONDS)
    );
    const id = window.setTimeout(timeout.action, boundedDelay);

    timeouts.value.set(id, timeout);

    return id;
  }

  function stopInterval(id: number) {
    clearInterval(id);
    intervals.value.delete(id);
  }

  function stopTimeout(id: number) {
    clearTimeout(id);
    timeouts.value.delete(id);
  }

  function stopAll() {
    intervals.value.forEach((_, key) => {
      clearInterval(key);
      intervals.value.delete(key);
    });

    timeouts.value.forEach((_, key) => {
      clearTimeout(key);
      timeouts.value.delete(key);
    });
  }

  return {
    registerInterval,
    registerTimeout,
    stopInterval,
    stopTimeout,
    stopAll,
  };
});

registerStoreHMR(useSchedulerStore);
