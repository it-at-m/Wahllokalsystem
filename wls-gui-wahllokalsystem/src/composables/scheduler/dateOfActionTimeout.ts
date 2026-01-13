import type { Ref } from "vue";

import { watch } from "vue";

import { useSchedulerStore } from "@/stores/schedulerStore.ts";

export function useDateOfActionTimeout(
  title: string,
  dateOfAction: Ref<Date | undefined>,
  callback: () => void
) {
  const { registerTimeout, stopTimeout } = useSchedulerStore();
  let popupTimeout: number | null = null;

  watch(dateOfAction, () => setupTimer());

  function setupTimer() {
    clearTimer();

    if (dateOfAction.value) {
      popupTimeout = registerTimeout({
        title: title,
        action: callback,
        dateOfAction: dateOfAction.value,
      });
    }
  }

  function clearTimer() {
    if (popupTimeout !== null) {
      stopTimeout(popupTimeout);
    }
  }

  return { clearTimer, setupTimer };
}
