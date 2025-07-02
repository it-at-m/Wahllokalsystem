import type { Ref } from "vue";

import { watch } from "vue";

const MAX_DELAY_IN_MILLISECONDS = 0x7fffffff; //https://mrcoles.com/maximum-delay-settimeout/

export function useDateOfActionTimeout(
  dateOfAction: Ref<Date | undefined>,
  callback: () => void
) {
  let popupTimeout: number | null = null;

  watch(dateOfAction, () => setupTimer());

  function setupTimer() {
    if (popupTimeout !== null) {
      clearTimeout(popupTimeout);
    }

    if (dateOfAction.value) {
      const currentTime = new Date().getTime();
      const popupTime = dateOfAction.value.getTime();
      const delayInMilliseconds = popupTime - currentTime;
      if (
        delayInMilliseconds >= 0 &&
        delayInMilliseconds <= MAX_DELAY_IN_MILLISECONDS
      ) {
        popupTimeout = window.setTimeout(callback, delayInMilliseconds);
      }
    }
  }

  setupTimer();

  return {};
}
