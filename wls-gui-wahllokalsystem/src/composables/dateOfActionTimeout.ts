import type { Ref } from "vue";

import { watch } from "vue";

const MAX_DELAY = 0x7fffffff; //https://mrcoles.com/maximum-delay-settimeout/

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
      const timeUntilPopup = popupTime - currentTime;
      if (timeUntilPopup >= 0 && timeUntilPopup < MAX_DELAY) {
        popupTimeout = window.setTimeout(callback, timeUntilPopup);
      }
    }
  }

  setupTimer();

  return {};
}
